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
import ch.ethz.seb.sps.server.ServiceInit;
import ch.ethz.seb.sps.server.datalayer.batis.custommappers.ScreenshotMapper;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordMapper;
import ch.ethz.seb.sps.server.servicelayer.ScreenshotStoreService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.apache.commons.io.IOUtils;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Lazy
@Component
@ConditionalOnExpression("'${sps.data.store.strategy}'.equals('SINGLE_STORE') and '${sps.data.store.adapter}'.equals('S3')")
public class ScreenshotSingleStore_S3 extends ScreenshotS3 implements ScreenshotStoreService {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotSingleStore_S3.class);

    private final SqlSessionFactory sqlSessionFactory;
    private final TransactionTemplate transactionTemplate;
    private final WebsocketDataExtractor websocketDataExtractor;
    private SqlSessionTemplate sqlSessionTemplate;
    private ScreenshotDataRecordMapper screenshotDataRecordMapper;
    private MinioClient minioClient;


    private final BlockingDeque<ScreenshotQueueData> screenshotDataQueue = new LinkedBlockingDeque<>();

    public ScreenshotSingleStore_S3(
            final SqlSessionFactory sqlSessionFactory,
            final PlatformTransactionManager transactionManager,
            final WebsocketDataExtractor websocketDataExtractor,
            final Environment environment,
            @Qualifier(value = ServiceConfig.SCREENSHOT_STORE_API_EXECUTOR) final TaskScheduler taskScheduler){

        super(environment);

        this.sqlSessionFactory = sqlSessionFactory;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.websocketDataExtractor = websocketDataExtractor;
    }

    @Override
    public void init() {
        ServiceInit.INIT_LOGGER.info("----> Screenshot Store Strategy SINGLE_STREAMING: initialized");

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
    }

    @Override
    public void storeScreenshot(
            final String sessionUUID,
            final Long timestamp,
            final ImageFormat imageFormat,
            final String metadata,
            final InputStream in) {

        uploadScreenshotToBucket(createDataObject(
                sessionUUID,
                timestamp,
                imageFormat,
                metadata,
                in
        ));
    }

    @Override
    public void storeScreenshot(final String sessionUUID, final InputStream in) {

    }


    @Override
    public int getStoreHealthIndicator() {
        return 0;
    }


    private void uploadScreenshotToBucket(ScreenshotQueueData data) {
        this.screenshotDataRecordMapper.insert(data.record);
        this.sqlSessionTemplate.flushStatements();

        try {
            String fileName = data.record.getSessionUuid() + "_" + data.record.getId();
            this.minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("sebserver-dev")
                            .object(fileName)
//                                        .stream(data.screenshotIn, data.screenshotIn.readAllBytes().length, -1)
                            .stream(data.screenshotIn, -1, 10485760)
                            .build());

        } catch (Exception e) {
            log.error("error" + e);
        }
        this.sqlSessionTemplate.flushStatements();
    }

    private ScreenshotQueueData createDataObject(
            final String sessionUUID,
            final Long timestamp,
            final ImageFormat imageFormat,
            final String metadata,
            final InputStream in){
        try {
            return new ScreenshotQueueData(
                    sessionUUID,
                    timestamp,
                    imageFormat,
                    metadata,
                    IOUtils.toByteArray(in));

        } catch (final Exception e) {
            log.error("Failed to get screenshot from InputStream for session: {}", sessionUUID, e);
            return null;
        }
    }

}