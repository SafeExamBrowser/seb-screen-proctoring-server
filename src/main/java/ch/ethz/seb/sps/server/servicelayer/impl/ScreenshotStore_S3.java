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
import ch.ethz.seb.sps.server.datalayer.batis.custommappers.ScreenshotMapper;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordMapper;
import ch.ethz.seb.sps.server.datalayer.dao.impl.S3DAO;
import ch.ethz.seb.sps.server.servicelayer.ScreenshotStoreService;
import ch.ethz.seb.sps.server.servicelayer.SessionServiceHealthControl;
import ch.ethz.seb.sps.utils.Constants;
import io.minio.SnowballObject;
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
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Lazy
@Component
@ConditionalOnExpression("'${sps.data.store.adapter}'.equals('S3_RDBMS')")
public class ScreenshotStore_S3 implements ScreenshotStoreService{

    private static final Logger log = LoggerFactory.getLogger(ScreenshotStore_S3.class);

    private final SqlSessionFactory sqlSessionFactory;
    private final TransactionTemplate transactionTemplate;
    private final TaskScheduler taskScheduler;

    private final boolean batchStore;
    private final long batchInterval;
    private final S3DAO s3DAO;
    private SqlSessionTemplate sqlSessionTemplate;
    private ScreenshotDataRecordMapper screenshotDataRecordMapper;

    private final BlockingDeque<ScreenshotQueueData> screenshotDataQueue = new LinkedBlockingDeque<>();

    public ScreenshotStore_S3(
            final SqlSessionFactory sqlSessionFactory,
            final PlatformTransactionManager transactionManager,
            final S3DAO s3DAO,
            @Qualifier(value = ServiceConfig.SCREENSHOT_STORE_API_EXECUTOR) final TaskScheduler taskScheduler,
            @Value("${sps.data.store.batch.interval:1000}") final long batchInterval,
            @Value("${sps.s3.store.batch:true}") final boolean batchStore) {

        this.sqlSessionFactory = sqlSessionFactory;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.taskScheduler = taskScheduler;
        this.batchStore = batchStore;
        this.batchInterval = batchInterval;
        this.s3DAO = s3DAO;
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

        // we run with two scheduled tasks that process the queue in half second interval
        final Collection<ScreenshotQueueData> data1 = new ArrayList<>();
        this.taskScheduler.scheduleWithFixedDelay(
                () -> processStore(data1),
                DateTime.now(DateTimeZone.UTC).toDate(),
                this.batchInterval);

        final Collection<ScreenshotQueueData> data2 = new ArrayList<>();
        this.taskScheduler.scheduleWithFixedDelay(
                () -> processStore(data2),
                DateTime.now(DateTimeZone.UTC).plus(500).toDate(),
                this.batchInterval);

    }

    @Override
    public int getStoreHealthIndicator() {
        final int size = this.screenshotDataQueue.size();
        if (size >= SessionServiceHealthControl.BATCH_SIZE_INDICATOR_MAP_MAX) {
            return 10;
        }
        return (int) (size / (float) SessionServiceHealthControl.BATCH_SIZE_INDICATOR_MAP_MAX * 10);
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
    }

    private void processStore(final Collection<ScreenshotQueueData> batch) {
        batch.clear();
        this.screenshotDataQueue.drainTo(batch);

        if (batch.isEmpty()) {
            return;
        }

        if (batchStore) {
            applyBatchStore(batch);
        } else {
            applySingleStore(batch);
        }
    }

    private void applySingleStore(final Collection<ScreenshotQueueData> batch) {
        // this stores the batch with single transactions and S3 store calls
        // probably less performant but in case S3 do not support batch store
        batch.forEach(data -> {
            try {
                // store screenshot data of single screenshot within DB
                screenshotDataRecordMapper.insert(data.record);
                this.sqlSessionTemplate.flushStatements();

                // store single screenshot picture to S3
                this.s3DAO.uploadItem(
                        data.screenshotIn,
                        data.record.getSessionUuid(),
                        data.record.getId()
                ).getOrThrow();

            } catch (Exception e) {
                log.error("Failed to single store screenshot data... put data back to queue. Cause: {}", e.getMessage());
                this.screenshotDataQueue.add(data);
            }
        });
    }

    private void applyBatchStore(final Collection<ScreenshotQueueData> batch) {
        try {
            this.transactionTemplate.executeWithoutResult(status -> {
                // store all screenshot data in batch and grab generated keys put back to records
                batch.forEach(data -> this.screenshotDataRecordMapper.insert(data.record));
                this.sqlSessionTemplate.flushStatements();

                // now store all screenshots within respective generated ids in batch
                List<SnowballObject> batchItems = new ArrayList<SnowballObject>();
                batch.forEach(data -> {
                    String fileName = data.record.getSessionUuid() + Constants.UNDERLINE + data.record.getId();

                    batchItems.add(
                            new SnowballObject(
                                    fileName,
                                    data.screenshotIn,
                                    data.screenshotIn.available(),
                                    null));
                });

                //upload batch to s3 store
                this.s3DAO.uploadItemBatch(batchItems).onError(e -> {
                    log.error("Failed to upload batch to S3 service. Transaction has failed... put data back to queue. Cause: ", e);
                    this.screenshotDataQueue.addAll(batch);
                });

            });

        } catch (final TransactionException te) {
            log.error("Failed to batch store screenshot data. Transaction has failed... put data back to queue. Cause: ", te);
            this.screenshotDataQueue.addAll(batch);
        }
    }

}
