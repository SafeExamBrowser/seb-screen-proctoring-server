/*
 * Copyright (c) 2025 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import ch.ethz.seb.sps.server.ServiceConfig;
import ch.ethz.seb.sps.server.ServiceInit;
import ch.ethz.seb.sps.server.datalayer.batis.custommappers.ScreenshotMapper;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataLiveCacheRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataLiveCacheRecordMapper;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordMapper;
import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataLiveCacheRecord;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDataLiveCacheDAO;
import ch.ethz.seb.sps.server.datalayer.dao.SessionDAO;
import ch.ethz.seb.sps.server.servicelayer.LiveProctoringCacheService;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;

@Lazy
@Component
public class LiveProctoringCacheServiceImpl implements LiveProctoringCacheService {

    private static final Logger log = LoggerFactory.getLogger(LiveProctoringCacheServiceImpl.class);
    public static final Logger INIT_LOGGER = LoggerFactory.getLogger("SERVICE_INIT");

    private final TaskScheduler taskScheduler;
    private final long batchInterval;
    
    private final SqlSessionFactory sqlSessionFactory;
    private final TransactionTemplate transactionTemplate;
    private final ScreenshotDataLiveCacheDAO screenshotDataLiveCacheDAO;
    private final SessionDAO sessionDAO;
    
    private SqlSessionTemplate sqlSessionTemplate;
    private ScreenshotDataLiveCacheRecordMapper screenshotDataLiveCacheRecordMapper;
    
    
    private final Map<String, Long> cache = new ConcurrentHashMap<>();

    public LiveProctoringCacheServiceImpl(
            final SqlSessionFactory sqlSessionFactory,
            final PlatformTransactionManager transactionManager,
            final ScreenshotDataLiveCacheDAO screenshotDataLiveCacheDAO,
            final SessionDAO sessionDAO,
            @Qualifier(value = ServiceConfig.SCREENSHOT_STORE_API_EXECUTOR) final TaskScheduler taskScheduler,
            @Value("${sps.data.store.batch.interval:1000}") final long batchInterval) {

        this.sqlSessionFactory = sqlSessionFactory;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.screenshotDataLiveCacheDAO = screenshotDataLiveCacheDAO;
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.sessionDAO = sessionDAO;
        this.taskScheduler = taskScheduler;
        this.batchInterval = batchInterval;
    }


    @Override
    public void init() {

        INIT_LOGGER.info("---->");
        INIT_LOGGER.info("----> Initialize Live Proctoring Cache Service");
        INIT_LOGGER.info("---->     with update interval: " + batchInterval);
        
        try {
            this.sqlSessionTemplate = new SqlSessionTemplate(this.sqlSessionFactory, ExecutorType.BATCH);

            final MapperRegistry mapperRegistry = this.sqlSessionTemplate.getConfiguration().getMapperRegistry();
            final Collection<Class<?>> mappers = mapperRegistry.getMappers();
            if (!mappers.contains(ScreenshotMapper.class)) {
                mapperRegistry.addMapper(ScreenshotMapper.class);
            }
            if (!mappers.contains(ScreenshotDataRecordMapper.class)) {
                mapperRegistry.addMapper(ScreenshotDataRecordMapper.class);
            }
            this.screenshotDataLiveCacheRecordMapper = this.sqlSessionTemplate.getMapper(ScreenshotDataLiveCacheRecordMapper.class);
            
            this.taskScheduler.scheduleWithFixedDelay(
                    this::updateFromStoreCache,
                    DateTime.now(DateTimeZone.UTC).toDate().toInstant(),
                    Duration.ofMillis(this.batchInterval));
           
        } catch (Exception e) {
            ServiceInit.INIT_LOGGER.error("----> Live Proctoring Cache Service : failed to initialized", e);
            throw e;
        }
    }

    @Override
    public Long getLatestSSDataId(final String sessionUUID, final boolean createSlot) {
        if (!cache.containsKey(sessionUUID) && createSlot) {

            if (!this.sessionDAO.isActive(sessionUUID)) {
                return null;
            }
            
            if (log.isDebugEnabled()) {
                log.debug("Create ad-hoc cache slot for active session: {}", sessionUUID);
            }
            
            synchronized (this.cache) {
                screenshotDataLiveCacheDAO
                        .createCacheEntry(sessionUUID)
                        .onError(error -> log.error("Failed to create slot for session: {}", sessionUUID));

                cache.put(sessionUUID, -1L);
            }
        }
        
        return cache.get(sessionUUID);
    }

    @Override
    public void updateCacheStore(final Collection<ScreenshotQueueData> batch) {
        try {
            
            if (log.isDebugEnabled()) {
                log.debug("Update store cache with batch of: {}", batch.size());
            }

            // filter incoming data so that if there are several screenshots for one session
            // in undefined order, it really took the last one and not override one that is later
            Map<String, Long> mapping = new HashMap<>();
            
            // then batch update
            this.transactionTemplate.executeWithoutResult(status -> {
                batch.forEach(data -> {
                    if (data.record.getId() != null) {
                        final String sessionId = data.record.getSessionUuid();
                        final Long timestamp = data.record.getTimestamp();
                        if (mapping.containsKey(sessionId) && timestamp != null && mapping.get(sessionId) > timestamp) {
                            return; // skip this one since we already have a newer
                        }

                        // add to update batch
                        UpdateDSL.updateWithMapper(
                                        screenshotDataLiveCacheRecordMapper::update,
                                        ScreenshotDataLiveCacheRecordDynamicSqlSupport.screenshotDataLiveCacheRecord)
                                .set(ScreenshotDataLiveCacheRecordDynamicSqlSupport.idLatestSsd).equalTo(data.record.getId())
                                .where(ScreenshotDataLiveCacheRecordDynamicSqlSupport.sessionUuid, isEqualTo(sessionId))
                                .build()
                                .execute();

                        // register as processed
                        mapping.put(sessionId, timestamp);
                    }
                });
                this.sqlSessionTemplate.flushStatements();

            });

        } catch (final TransactionException te) {
            log.error("Failed to batch update screenshot data live cache store. Transaction has failed. Cause: {}", te.getMessage());
        }
    }

    @Override
    public void cleanup(final boolean isMaster) {
        try {
            
            if (log.isTraceEnabled()) {
                log.debug("Cleanup live session cache");
            }
            
            // if master cleanup the store cache
            if (isMaster) {
                List<String> closedSession = sessionDAO
                        .getAllClosedSessionsIn(cache.keySet())
                        .getOrThrow();
                
                if (!closedSession.isEmpty()) {
                    
                    if (log.isDebugEnabled()) {
                        log.debug("Delete all closed sessions form live screenshot cache storage: {}", closedSession);
                    }
                    
                    screenshotDataLiveCacheDAO
                            .deleteAll(closedSession)
                            .getOrThrow();
                }
            }

            // just cleanup the local cache
            Set<String> openSession = screenshotDataLiveCacheDAO
                    .getAll()
                    .getOrThrow()
                    .stream()
                    .map(ScreenshotDataLiveCacheRecord::getSessionUuid)
                    .collect(Collectors.toSet());


            Set<String> cacheKeys = new HashSet<>(cache.keySet());
            cacheKeys.forEach(key -> {
                if (!openSession.contains(key)) {
                    
                    if (log.isDebugEnabled()) {
                        log.debug("Clear entry from local cache for session: {}", key);
                    }
                    
                    cache.remove(key);
                }
            });
            
            
        } catch (Exception e) {
            log.error("Failed to cleanup cache: ", e);
        }
    }
    
    private void updateFromStoreCache() {
        try {

            Map<String, Long> newValues = screenshotDataLiveCacheDAO
                    .getAll()
                    .getOrThrow()
                    .stream()
                    .collect(Collectors.toMap(
                            ScreenshotDataLiveCacheRecord::getSessionUuid,
                            rec -> rec.getIdLatestSsd() == null ? -1L : rec.getIdLatestSsd()
                    ));

            cache.putAll(newValues);

        } catch (Exception e) {
            log.error("Failed to update cache: ", e);
        }
    }
}
