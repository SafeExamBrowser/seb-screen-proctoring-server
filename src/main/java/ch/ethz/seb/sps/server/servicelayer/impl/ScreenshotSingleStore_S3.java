/*
 * Copyright (c) 2024 ETH Zürich, IT Services
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
import ch.ethz.seb.sps.server.datalayer.dao.impl.S3DAO;
import ch.ethz.seb.sps.server.servicelayer.ScreenshotStoreService;
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

@Lazy
@Component
@ConditionalOnExpression("'${sps.data.store.strategy}'.equals('SINGLE_STORE') and '${sps.data.store.adapter}'.equals('S3_RDBMS')")
public class ScreenshotSingleStore_S3 implements ScreenshotStoreService {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotSingleStore_S3.class);
    private final SqlSessionFactory sqlSessionFactory;
    private final TransactionTemplate transactionTemplate;
    private final S3DAO s3DAO;
    private SqlSessionTemplate sqlSessionTemplate;
    private ScreenshotDataRecordMapper screenshotDataRecordMapper;

    public ScreenshotSingleStore_S3(
            final SqlSessionFactory sqlSessionFactory,
            final PlatformTransactionManager transactionManager,
            final Environment environment,
            final S3DAO s3DAO,
            @Qualifier(value = ServiceConfig.SCREENSHOT_STORE_API_EXECUTOR) final TaskScheduler taskScheduler){

        this.sqlSessionFactory = sqlSessionFactory;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.s3DAO = s3DAO;
    }

    @Override
    public void init() {
        ServiceInit.INIT_LOGGER.info("----> Screenshot Store Strategy SINGLE_STORE: initialized");

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
            s3DAO.uploadItem(data.screenshotIn, data.record.getSessionUuid(), data.record.getId());

        } catch (Exception e) {
            log.error("Failed to upload item to S3 service. Transaction has failed... Cause: ", e);

        }
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