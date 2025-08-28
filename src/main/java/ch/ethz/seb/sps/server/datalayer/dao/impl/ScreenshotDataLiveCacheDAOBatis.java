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
import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataLiveCacheRecord;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDataLiveCacheDAO;
import ch.ethz.seb.sps.utils.Result;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScreenshotDataLiveCacheDAOBatis implements ScreenshotDataLiveCacheDAO {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotDataLiveCacheDAOBatis.class);

    private final ScreenshotDataLiveCacheRecordMapper screenshotDataLiveCacheRecordMapper;

    public ScreenshotDataLiveCacheDAOBatis(ScreenshotDataLiveCacheRecordMapper screenshotDataLiveCacheRecordMapper) {
        this.screenshotDataLiveCacheRecordMapper = screenshotDataLiveCacheRecordMapper;
    }

    @Override
    @Transactional
    public Result<ScreenshotDataLiveCacheRecord> createCacheEntry(String sessionUUID) {
        return Result
            .tryCatch(() ->  screenshotDataLiveCacheRecordMapper.selectByPrimaryKey(createSlot(sessionUUID, -1L)))
            .onError(TransactionHandler::rollback);
    }

    @Override
    @Transactional
    public Result<ScreenshotDataLiveCacheRecord> createCacheEntry(String sessionUUID, Long value) {
        return Result
            .tryCatch(() ->  screenshotDataLiveCacheRecordMapper.selectByPrimaryKey(createSlot(sessionUUID, value)))
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
        return Result.tryCatch(() -> screenshotDataLiveCacheRecordMapper.selectByExample().build().execute());
    }

    private synchronized Long createSlot(String sessionUUID, Long value) {
        final ScreenshotDataLiveCacheRecord rec = new ScreenshotDataLiveCacheRecord(
                null,
                sessionUUID,
                value );

        screenshotDataLiveCacheRecordMapper.insert(rec);
        return rec.getId();
    }
}
