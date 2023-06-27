/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.commons.io.IOUtils;
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
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;

import com.fasterxml.jackson.core.type.TypeReference;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.JSONMapper;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import ch.ethz.seb.sps.server.ServiceConfig;
import ch.ethz.seb.sps.server.ServiceInit;
import ch.ethz.seb.sps.server.datalayer.batis.BatisConfig;
import ch.ethz.seb.sps.server.datalayer.batis.ScreenshotMapper;
import ch.ethz.seb.sps.server.datalayer.batis.ScreenshotMapper.BlobContent;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordMapper;
import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataRecord;
import ch.ethz.seb.sps.server.servicelayer.ScreenshotStoreService;
import ch.ethz.seb.sps.utils.Utils;

@Lazy
@Component
@Import(BatisConfig.class)
@ConditionalOnExpression("'${sps.data.store.strategy}'.equals('BATCH_STORE') and '${sps.data.store.adapter}'.equals('FULL_RDBMS')")
public class ScreenshotBatchStore_FullRDBMS implements ScreenshotStoreService {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotBatchStore_FullRDBMS.class);

    private final SqlSessionFactory sqlSessionFactory;
    private final TransactionTemplate transactionTemplate;
    private final JSONMapper jsonMapper;
    private final TaskScheduler taskScheduler;
    private final long batchInterval;

    private final SqlSessionTemplate sqlSessionTemplate;
    private final ScreenshotMapper screenshotMapper;
    private final ScreenshotDataRecordMapper screenshotDataRecordMapper;

    private final BlockingDeque<ScreenshotData> screenshotDataQueue = new LinkedBlockingDeque<>();

    public ScreenshotBatchStore_FullRDBMS(
            final SqlSessionFactory sqlSessionFactory,
            final PlatformTransactionManager transactionManager,
            final JSONMapper jsonMapper,
            @Qualifier(value = ServiceConfig.SCREENSHOT_STORE_API_EXECUTOR) final TaskScheduler taskScheduler,
            @Value("${sps.data.store..batch.interval:1000}") final long batchInterval) {

        this.sqlSessionFactory = sqlSessionFactory;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.jsonMapper = jsonMapper;
        this.taskScheduler = taskScheduler;
        this.batchInterval = batchInterval;

        this.sqlSessionTemplate = new SqlSessionTemplate(this.sqlSessionFactory, ExecutorType.BATCH);
        this.screenshotMapper = this.sqlSessionTemplate.getMapper(ScreenshotMapper.class);
        this.screenshotDataRecordMapper = this.sqlSessionTemplate.getMapper(ScreenshotDataRecordMapper.class);

    }

    @Override
    public void init() {

        try {

            final Collection<ScreenshotData> data1 = new ArrayList<>();
            this.taskScheduler.scheduleWithFixedDelay(
                    () -> processBatchStore(data1),
                    DateTime.now(DateTimeZone.UTC).toDate(),
                    this.batchInterval);

            final Collection<ScreenshotData> data2 = new ArrayList<>();
            this.taskScheduler.scheduleWithFixedDelay(
                    () -> processBatchStore(data2),
                    DateTime.now(DateTimeZone.UTC).plus(500).toDate(),
                    this.batchInterval);

            ServiceInit.INIT_LOGGER.info(
                    "----> Screenshot Store Strategy BATCH_STORE: 2 workers with update-interval: {} initialized",
                    this.batchInterval);

        } catch (final Exception e) {
            ServiceInit.INIT_LOGGER.error(
                    "----> Screenshot Store Strategy BATCH_STORE: failed to initialized", e);
        }

    }

    @Override
    public int getStoreHealthIndicator() {
        final int size = this.screenshotDataQueue.size();
        return (size < 100) ? 0 : (size < 300) ? 1 : (size < 500) ? 2 : 3;
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
            this.screenshotDataQueue.add(new ScreenshotData(
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
        try {

            // get fist two bytes with meta date length
            final byte[] lBytes = new byte[2];
            in.read(lBytes);
            final short metaLengh = ByteBuffer.wrap(lBytes).getShort(); // big-endian by default

            // now get the meta data
            final byte[] meta = new byte[metaLengh];
            in.read(meta);
            final String metaDataJson = Utils.toString(meta);

            final HashMap<String, String> metaDataMap = this.jsonMapper.readValue(
                    metaDataJson,
                    new TypeReference<HashMap<String, String>>() {
                    });

            final Long timestamp = Long.valueOf(metaDataMap.remove(Domain.SCREENSHOT_DATA.ATTR_TIMESTAMP));
            final ImageFormat format = ImageFormat.byName(metaDataMap.remove(Domain.SCREENSHOT_DATA.ATTR_IMAGE_FORMAT));

            storeScreenshot(sessionUUID, timestamp, format, this.jsonMapper.writeValueAsString(metaDataMap), in);

        } catch (final Exception e) {
            log.error("Failed to store screenshot: ", e);
        }
    }

    private void processBatchStore(final Collection<ScreenshotData> batch) {

        long start = 0L;
        if (log.isDebugEnabled()) {
            start = Utils.getMillisecondsNow();
        }

        final int size = this.screenshotDataQueue.size();
        if (size > 1000) {
            log.warn("-----> There are more then 1000 screenshots in the waiting queue: {}, worker: {}",
                    size,
                    Thread.currentThread().getName());
        }

        if (size == 0) {
            return;
        }

        try {

            batch.clear();
            this.screenshotDataQueue.drainTo(batch);

            if (batch.isEmpty()) {
                return;
            }

            storeScreenshotsInDB(batch);

//            if (this.serviceInfo.isFileStore()) {
//                storeScreenshotsInFileSystem(batch);
//            } else if (this.serviceInfo.isFullRDBMSStore()) {
//                storeScreenshotsInDB(batch);
//            } else {
//                throw new IllegalArgumentException("No screenshot store adapter found");
//            }

            if (log.isDebugEnabled()) {
                log.debug("BatchStoreScreenshotServiceImpl worker {} processes batch of size {} in {} ms",
                        Thread.currentThread().getName(),
                        size,
                        Utils.getMillisecondsNow() - start);
            }

        } catch (final Exception e) {
            log.error("Failed to process screenshot batch store: ", e);
        }

    }

    private void storeScreenshotsInDB(final Collection<ScreenshotData> batch) {

        try {
            this.transactionTemplate
                    .executeWithoutResult(status -> {

                        // store all screenshot data in batch and grab generated keys put back to records
                        batch.stream().forEach(data -> this.screenshotDataRecordMapper.insert(data.record));
                        this.sqlSessionTemplate.flushStatements();

                        // now store all screenshots within respective generated ids in batch
                        batch.stream().forEach(
                                data -> this.screenshotMapper.insert(new BlobContent(
                                        data.record.getId(),
                                        data.screenshotIn)));
                        this.sqlSessionTemplate.flushStatements();
                    });
        } catch (final TransactionException te) {
            log.error(
                    "Failed to batch store screenshot data. Transaction has failed... put data back to queue. Cause: ",
                    te);
            this.screenshotDataQueue.addAll(batch);
        }
    }

//    private void storeScreenshotsInFileSystem(final Collection<ScreenshotData> batch) {
//
//        try {
//            this.transactionTemplate
//                    .executeWithoutResult(status -> {
//
//                        // store all screenshot data in batch and grab generated keys put back to records
//                        batch.stream().forEach(data -> this.screenshotDataRecordMapper.insert(data.record));
//                        this.sqlSessionTemplate.flushStatements();
//
//                    });
//        } catch (final TransactionException te) {
//            log.error(
//                    "Failed to batch store screenshot data. Transaction has failed... put data back to queue. Cause: ",
//                    te);
//            this.screenshotDataQueue.addAll(batch);
//        }
//
//        // TODO we cannot batch store on file-system here or maybe? how to?
//        batch.stream().forEach(data -> this.screenshotDAO.storeImage(
//                data.record.getId(),
//                data.record.getSessionUuid(),
//                data.screenshotIn)
//                .onError(error -> log.error("Failed to store screenshot on file-system for: {}", data, error)));
//    }

    private static final class ScreenshotData {
        final ScreenshotDataRecord record;
        final ByteArrayInputStream screenshotIn;

        public ScreenshotData(
                final String sessionUUID,
                final Long timestamp,
                final ImageFormat imageFormat,
                final String metadata,
                final byte[] screenshot) {

            this.record = new ScreenshotDataRecord(
                    null,
                    sessionUUID,
                    timestamp,
                    imageFormat != null ? imageFormat.code : null,
                    metadata);

            this.screenshotIn = new ByteArrayInputStream(screenshot);
        }
    }

}
