/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import ch.ethz.seb.sps.server.ServiceConfig;
import ch.ethz.seb.sps.server.datalayer.batis.custommappers.ScreenshotMapper;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordMapper;
import ch.ethz.seb.sps.server.servicelayer.ScreenshotStoreService;
import ch.ethz.seb.sps.server.servicelayer.SessionServiceHealthControl;
import io.minio.MinioClient;
import io.minio.SnowballObject;
import io.minio.UploadObjectArgs;
import io.minio.UploadSnowballObjectsArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.messages.Bucket;
import org.apache.commons.io.IOUtils;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Lazy
@Component
@ConditionalOnExpression("'${sps.data.store.strategy}'.equals('BATCH_STORE') and '${sps.data.store.adapter}'.equals('S3')")
public class ScreenshotBatchStore_S3 extends ScreenshotS3 implements ScreenshotStoreService{

    private static final Logger log = LoggerFactory.getLogger(ScreenshotBatchStore_S3.class);

    private final SqlSessionFactory sqlSessionFactory;
    private final TransactionTemplate transactionTemplate;
    private final WebsocketDataExtractor websocketDataExtractor;
    private final TaskScheduler taskScheduler;
    private final long batchInterval;

    private SqlSessionTemplate sqlSessionTemplate;
    private ScreenshotDataRecordMapper screenshotDataRecordMapper;


    private final BlockingDeque<ScreenshotQueueData> screenshotDataQueue = new LinkedBlockingDeque<>();

    public ScreenshotBatchStore_S3(
            final SqlSessionFactory sqlSessionFactory,
            final PlatformTransactionManager transactionManager,
            final WebsocketDataExtractor websocketDataExtractor,
            final Environment environment,
            @Qualifier(value = ServiceConfig.SCREENSHOT_STORE_API_EXECUTOR) final TaskScheduler taskScheduler,
            @Value("${sps.data.store.batch.interval:1000}") final long batchInterval) {

        super(environment);

        this.sqlSessionFactory = sqlSessionFactory;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.websocketDataExtractor = websocketDataExtractor;
        this.taskScheduler = taskScheduler;
        this.batchInterval = batchInterval;
    }

    @Override
    public void init() {
        this.sqlSessionTemplate = new SqlSessionTemplate(this.sqlSessionFactory, ExecutorType.BATCH);

        final MapperRegistry mapperRegistry = this.sqlSessionTemplate.getConfiguration().getMapperRegistry();
        final Collection<Class<?>> mappers = mapperRegistry.getMappers();
        if (!mappers.contains(ScreenshotMapper.class)) {
            mapperRegistry.addMapper(ScreenshotMapper.class);
        }
        if (!mappers.contains(ScreenshotDataRecordMapper.class)) {
            mapperRegistry.addMapper(ScreenshotDataRecordMapper.class);
        }

        this.screenshotDataRecordMapper = this.sqlSessionTemplate.getMapper(ScreenshotDataRecordMapper.class);

        final Collection<ScreenshotQueueData> data1 = new ArrayList<>();
        this.taskScheduler.scheduleWithFixedDelay(
                () -> processBatchStore(data1),
                DateTime.now(DateTimeZone.UTC).toDate(),
                this.batchInterval);

        final Collection<ScreenshotQueueData> data2 = new ArrayList<>();
        this.taskScheduler.scheduleWithFixedDelay(
                () -> processBatchStore(data2),
                DateTime.now(DateTimeZone.UTC).plus(500).toDate(),
                this.batchInterval);

//        try {
//            printBuckets();
//            testUploadFile();
//        } catch (final Exception e) {
//        }


    }

    private void printBuckets() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<Bucket> bucketList = this.minioClient.listBuckets();
        for (Bucket bucket : bucketList) {
            System.out.println(bucket.creationDate() + ", " + bucket.name());
        }
    }

    private void testUploadFile() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        this.minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket("sebserver-dev")
                        .object("testId")
                        .filename("src/main/java/ch/ethz/seb/sps/server/servicelayer/impl/testFile.txt")
                        .build());
    }

    @Override
    public int getStoreHealthIndicator() {
        final int size = this.screenshotDataQueue.size();
        if (size >= SessionServiceHealthControl.BATCH_STORE_SIZE_INDICATOR_MAP_MAX) {
            return 10;
        }
        return (int) (size / (float) SessionServiceHealthControl.BATCH_STORE_SIZE_INDICATOR_MAP_MAX * 10);
    }

    public void processOneTime() {
        processBatchStore(new ArrayList<>());
    }

    @Override
    public void storeScreenshot(
            final String sessionUUID,
            final Long timestamp,
            final ImageFormat imageFormat,
            final String metadata,
            final InputStream in) {

        try {
            this.screenshotDataQueue.add(new ScreenshotQueueData(
                    sessionUUID,
                    timestamp,
                    imageFormat,
                    metadata,
                    IOUtils.toByteArray(in)));

        } catch (final Exception e) {
            log.error("Failed to get screenshot from InputStream for session: {}", sessionUUID, e);
        }
    }

    @Override
    public void storeScreenshot(final String sessionUUID, final InputStream in) {
//        this.websocketDataExtractor.storeScreenshot(sessionUUID, in, this);
    }

    private void processBatchStore(final Collection<ScreenshotQueueData> batch) {
        batch.clear();
        this.screenshotDataQueue.drainTo(batch);

        if (batch.isEmpty()) {
            return;
        }

        applyBatchStore(batch);
    }

    private void applyBatchStore(final Collection<ScreenshotQueueData> batch) {
        try {
            this.transactionTemplate.executeWithoutResult(status -> {
                // store all screenshot data in batch and grab generated keys put back to records
                batch.stream().forEach(data -> this.screenshotDataRecordMapper.insert(data.record));
                this.sqlSessionTemplate.flushStatements();

                //==================================================//
                //there are two ways to store data in a bucket

                //upload single object: https://min.io/docs/minio/linux/developers/java/API.html#uploadobject-uploadobjectargs-args
                //multiple calls --> iterates through the batch and calls the bucket for each item
//                batch.stream().forEach(data -> {
//                    try {
//                        String fileName = data.record.getSessionUuid() + "_" + data.record.getId();
//                        this.minioClient.putObject(
//                                PutObjectArgs.builder()
//                                        .bucket("sebserver-dev")
//                                        .object(fileName)
////                                        .stream(data.screenshotIn, data.screenshotIn.readAllBytes().length, -1)
//                                        .stream(data.screenshotIn, -1, 10485760)
//                                        .build());
//
//                    } catch (Exception e) {
//                        log.error("error" + e);
//                    }
//                });
//                this.sqlSessionTemplate.flushStatements();


                //upload multiple objects in one call: https://min.io/docs/minio/linux/developers/java/API.html#uploadsnowballobjects-uploadsnowballobjectsargs-args
                List<SnowballObject> batchItems = new ArrayList<SnowballObject>();
                batch.stream().forEach(data -> {
//                ScreenshotQueueData testSingleItem = batch.stream().collect(Collectors.toList()).get(0);
//                    String fileName = testSingleItem.record.getSessionUuid() + "_" + testSingleItem.record.getId();
                    String fileName = data.record.getSessionUuid() + "_" + data.record.getId();

                    byte[] screenshotBytes = data.screenshotIn.readAllBytes();
                    System.out.println(screenshotBytes.length);

                    batchItems.add(
                            new SnowballObject(
                                    fileName,
                                    new ByteArrayInputStream(screenshotBytes),
                                    screenshotBytes.length,
                                    null));
                });

                try {
                    minioClient.uploadSnowballObjects(
                            UploadSnowballObjectsArgs.builder()
                                    .bucket("sebserver-dev")
                                    .objects(batchItems)
                                    .build());

                } catch (Exception e) {
                    log.error("error: " + e);
                }
                this.sqlSessionTemplate.flushStatements();

            });

        } catch (final TransactionException te) {
            log.error("Failed to batch store screenshot data. Transaction has failed... put data back to queue. Cause: ", te);
        }
    }

}
