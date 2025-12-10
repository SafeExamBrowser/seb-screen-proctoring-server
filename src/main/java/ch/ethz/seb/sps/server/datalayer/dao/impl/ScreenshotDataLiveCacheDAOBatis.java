/*
 * Copyright (c) 2025 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao.impl;

import java.util.Collection;
import java.util.List;

import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataLiveCacheRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataLiveCacheRecordMapper;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordMapper;
import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataLiveCacheRecord;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDataLiveCacheDAO;
import ch.ethz.seb.sps.utils.Result;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordDynamicSqlSupport.sessionUuid;

@Service
public class ScreenshotDataLiveCacheDAOBatis implements ScreenshotDataLiveCacheDAO {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotDataLiveCacheDAOBatis.class);

    private final ScreenshotDataLiveCacheRecordMapper screenshotDataLiveCacheRecordMapper;
    private final ScreenshotDataRecordMapper screenshotDataRecordMapper;

    public ScreenshotDataLiveCacheDAOBatis(
            final ScreenshotDataLiveCacheRecordMapper screenshotDataLiveCacheRecordMapper,
            final ScreenshotDataRecordMapper screenshotDataRecordMapper) {

        this.screenshotDataLiveCacheRecordMapper = screenshotDataLiveCacheRecordMapper;
        this.screenshotDataRecordMapper = screenshotDataRecordMapper;
    }

    @Override
    @Transactional
    public Result<ScreenshotDataLiveCacheRecord> createCacheEntry(String sessionUUID) {
        return Result
            .tryCatch(() -> {
                Long slotId = createSlot(sessionUUID);
                if (slotId == null) {
                    throw new RuntimeException("Failed to get or create live cache slot for session: " + sessionUUID);
                }
                return screenshotDataLiveCacheRecordMapper.selectByPrimaryKey(slotId);
            })
            .onError(TransactionHandler::rollback);
    }


    @Override
    @Transactional
    public Result<String> deleteCacheEntry(String sessionUUID) {
        return Result.tryCatch(() -> {
            screenshotDataLiveCacheRecordMapper
                    .deleteByExample()
                    .where(
                            ScreenshotDataLiveCacheRecordDynamicSqlSupport.sessionUuid, 
                            SqlBuilder.isEqualTo(sessionUUID))
                    .build()
                    .execute();
            
            return sessionUUID;
        }).
            onError(TransactionHandler::rollback);
    }

    @Override
    @Transactional
    public Result<List<String>> deleteAll(List<String> sessionUUIDs) {
        return Result.tryCatch(() -> {
            
            screenshotDataLiveCacheRecordMapper
                    .deleteByExample()
                    .where(
                            ScreenshotDataLiveCacheRecordDynamicSqlSupport.sessionUuid, 
                            SqlBuilder.isIn(sessionUUIDs))
                    .build()
                    .execute();

            return sessionUUIDs;
        }).
                onError(TransactionHandler::rollback);
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<ScreenshotDataLiveCacheRecord>> getAll() {
        return Result.tryCatch(() -> {
            final List<ScreenshotDataLiveCacheRecord> execute = screenshotDataLiveCacheRecordMapper
                    .selectByExample()
                    .build()
                    .execute();
            return execute;
        });
    }

    private synchronized Long createSlot(String sessionUUID) {
        List<ScreenshotDataLiveCacheRecord> existing = screenshotDataLiveCacheRecordMapper
                .selectByExample()
                .where(
                        ScreenshotDataLiveCacheRecordDynamicSqlSupport.sessionUuid,
                        SqlBuilder.isEqualTo(sessionUUID))
                .build()
                .execute();

        if (existing != null && !existing.isEmpty()) {
            // we have already an entry, we can use that
            if (existing.size() > 1) {
                log.warn("Expected one cache entry for session: {} but found: {}", sessionUUID, existing.size());
            }

            ScreenshotDataLiveCacheRecord rec = existing.get(0);
            if (rec.getIdLatestSsd() != null) {
                return rec.getId();
            } else {
                // get value, update with value and return PK
                Long lastScreenshotEntryId = getLastScreenshotEntryId(sessionUUID);
                if (lastScreenshotEntryId != null) {
                    screenshotDataLiveCacheRecordMapper.updateByPrimaryKey(new ScreenshotDataLiveCacheRecord(
                            rec.getId(),
                            rec.getSessionUuid(),
                            lastScreenshotEntryId
                    ));
                    return rec.getId();
                } else {
                    return null;
                }
            }
        } else {
            // get value create slot with value and return PK
            Long lastScreenshotEntryId = getLastScreenshotEntryId(sessionUUID);
            if (lastScreenshotEntryId != null) {
                ScreenshotDataLiveCacheRecord rec = new ScreenshotDataLiveCacheRecord(
                        null,
                        sessionUUID,
                        lastScreenshotEntryId
                );
                screenshotDataLiveCacheRecordMapper.insert(rec);
                return rec.getId();
            } else {
                return null;
            }
        }
    }

    private Long getLastScreenshotEntryId(String sessionUUID) {
        try {

            List<Long> all = screenshotDataRecordMapper
                    .selectIdsByExample()
                    .where(sessionUuid, SqlBuilder.isEqualTo(sessionUUID))
                    .build()
                    .execute();

            if (all != null) {
                return all.get(all.size() - 1);
            }

            log.warn("No screenshot entry found for session: {}", sessionUUID);
            return null;
        } catch (Exception e) {
            log.error("Failed to get last screenshot entry for session: {} error: {}", sessionUUID, e.getMessage());
            return null;
        }
    }
}
