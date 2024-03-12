/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

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
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;

import ch.ethz.seb.sps.domain.model.service.ScreenshotData;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import ch.ethz.seb.sps.server.ServiceConfig;
import ch.ethz.seb.sps.server.ServiceInit;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordMapper;
import ch.ethz.seb.sps.server.servicelayer.ScreenshotStoreService;
import ch.ethz.seb.sps.utils.Utils;

@Lazy
@Component
@ConditionalOnExpression("'${sps.data.store.strategy}'.equals('BATCH_STORE') and '${sps.data.store.adapter}'.equals('FILESYS_RDBMS')")
public class ScreenshotBatchStore_FileRDBMS implements ScreenshotStoreService {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotBatchStore_FileRDBMS.class);

    private final SqlSessionFactory sqlSessionFactory;
    private final TransactionTemplate transactionTemplate;
    private final WebsocketDataExtractor websocketDataExtractor;
    private final TaskScheduler taskScheduler;
    private final long batchInterval;
    private final String rootDir;

    private SqlSessionTemplate sqlSessionTemplate;
    private ScreenshotDataRecordMapper screenshotDataRecordMapper;

    private final BlockingDeque<ScreenshotQueueData> screenshotDataQueue = new LinkedBlockingDeque<>();

    public ScreenshotBatchStore_FileRDBMS(
            final SqlSessionFactory sqlSessionFactory,
            final PlatformTransactionManager transactionManager,
            final WebsocketDataExtractor websocketDataExtractor,
            @Qualifier(value = ServiceConfig.SCREENSHOT_STORE_API_EXECUTOR) final TaskScheduler taskScheduler,
            @Value("${sps.data.store.batch.interval:1000}") final long batchInterval,
            @Value("${sps.data.store.file.dir:/screenshots/}") final String rootDir) {

        this.sqlSessionFactory = sqlSessionFactory;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.websocketDataExtractor = websocketDataExtractor;
        this.taskScheduler = taskScheduler;
        this.batchInterval = batchInterval;
        this.rootDir = rootDir;
    }

    @Override
    public void init() {
        try {

            this.sqlSessionTemplate = new SqlSessionTemplate(this.sqlSessionFactory, ExecutorType.BATCH);

            final MapperRegistry mapperRegistry = this.sqlSessionTemplate.getConfiguration().getMapperRegistry();
            final Collection<Class<?>> mappers = mapperRegistry.getMappers();
            if (!mappers.contains(ScreenshotDataRecordMapper.class)) {
                mapperRegistry.addMapper(ScreenshotDataRecordMapper.class);
            }

            this.screenshotDataRecordMapper = this.sqlSessionTemplate
                    .getMapper(ScreenshotDataRecordMapper.class);

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

            ServiceInit.INIT_LOGGER.info(
                    "----> Screenshot Store Strategy BATCH_STORE:FILESYS_RDBMS: 2 workers with update-interval: {} initialized",
                    this.batchInterval);

        } catch (final Exception e) {
            ServiceInit.INIT_LOGGER.error(
                    "----> Screenshot Store Strategy BATCH_STORE:FILESYS_RDBMS: failed to initialized", e);
        }
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
        this.websocketDataExtractor.storeScreenshot(sessionUUID, in, this);
    }

    @Override
    public int getStoreHealthIndicator() {
        final int size = this.screenshotDataQueue.size();
        return (size < 100) ? 0 : (size < 300) ? 1 : (size < 500) ? 2 : 3;
    }

    private void processBatchStore(final Collection<ScreenshotQueueData> batch) {
        long start = 0L;
        if (log.isDebugEnabled()) {
            start = Utils.getMillisecondsNow();
        }

        final int size = this.screenshotDataQueue.size();
        if (size > 500) {
            log.warn("-----> There are more then 500 screenshots in the waiting queue: {}, worker: {}",
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

            applyBatchStore(batch);

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

    private void applyBatchStore(final Collection<ScreenshotQueueData> batch) {
        try {
            this.transactionTemplate
                    .executeWithoutResult(status -> {

                        // store all screenshot data in batch and grab generated keys put back to records
                        batch.stream().forEach(data -> this.screenshotDataRecordMapper.insert(data.record));
                        this.sqlSessionTemplate.flushStatements();

                        // now store all screenshots within the file system
                        batch.stream().forEach(this::storeImage);

                    });
        } catch (final TransactionException te) {
            log.error(
                    "Failed to batch store screenshot data. Transaction has failed... put data back to queue. Cause: ",
                    te);
            this.screenshotDataQueue.addAll(batch);
        }
    }

    public void storeImage(final ScreenshotQueueData data) {

        OutputStream outputStream = null;

        try {

            final String dir = this.rootDir + data.record.getSessionUuid() + "/";
            final String fileName = ScreenshotData.SCREEN_PREFIX + data.record.getId();

            final FileSystemResource fileSystemResource = new FileSystemResource(dir);
            if (!fileSystemResource.exists()) {
                fileSystemResource.getFile().mkdirs();
            }

            final FileSystemResource fileResource = (FileSystemResource) fileSystemResource.createRelative(fileName);
            if (!fileResource.exists()) {
                fileResource.getFile().createNewFile();
            }

            outputStream = fileResource.getOutputStream();

            IOUtils.copy(data.screenshotIn, outputStream);

        } catch (final Exception e) {

        } finally {
            if (outputStream != null) {
                IOUtils.closeQuietly(outputStream);
            }
        }
    }

}
