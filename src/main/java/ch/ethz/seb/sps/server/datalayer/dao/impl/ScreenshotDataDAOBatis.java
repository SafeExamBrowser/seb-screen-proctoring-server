/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao.impl;

import static ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;
import static org.mybatis.dynamic.sql.SqlBuilder.isLikeWhenPresent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.select.SelectDSL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.service.ScreenshotData;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordMapper;
import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataRecord;
import ch.ethz.seb.sps.server.datalayer.dao.NoResourceFoundException;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDataDAO;
import ch.ethz.seb.sps.utils.Result;

@Service
public class ScreenshotDataDAOBatis implements ScreenshotDataDAO {

    private final ScreenshotDataRecordMapper screenshotDataRecordMapper;

    public ScreenshotDataDAOBatis(final ScreenshotDataRecordMapper screenshotDataRecordMapper) {
        this.screenshotDataRecordMapper = screenshotDataRecordMapper;
    }

    @Override
    public EntityType entityType() {
        return EntityType.SCREENSHOT_DATA;
    }

    @Override
    @Transactional(readOnly = true)
    public Result<ScreenshotData> byPK(final Long id) {
        return Result.tryCatch(() -> this.screenshotDataRecordMapper.selectByPrimaryKey(id))
                .map(this::toDomainModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<ScreenshotData>> allOf(final Set<Long> pks) {
        return Result.tryCatch(() -> {

            if (pks == null || pks.isEmpty()) {
                return Collections.emptyList();
            }

            return this.screenshotDataRecordMapper
                    .selectByExample()
                    .where(ScreenshotDataRecordDynamicSqlSupport.id, isIn(new ArrayList<>(pks)))
                    .build()
                    .execute()
                    .stream()
                    .map(this::toDomainModel)
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<ScreenshotData>> allOfSession(final String sessionUUID) {
        return Result.tryCatch(() -> this.screenshotDataRecordMapper.selectByExample()
                .where(ScreenshotDataRecordDynamicSqlSupport.sessionUuid, SqlBuilder.isEqualTo(sessionUUID))
                .build()
                .execute()
                .stream()
                .map(this::toDomainModel)
                .collect(Collectors.toList()));
    }

    @Override
    @Transactional(readOnly = true)
    public Result<ScreenshotDataRecord> getAt(final String sessionUUID, final Long at) {
        return Result.tryCatch(() -> {
            return SelectDSL
                    .selectWithMapper(this.screenshotDataRecordMapper::selectOne,
                            id,
                            sessionUuid,
                            timestamp,
                            imageFormat,
                            metaData,
                            SqlBuilder.max(timestamp))
                    .from(screenshotDataRecord)
                    .where(ScreenshotDataRecordDynamicSqlSupport.sessionUuid, SqlBuilder.isEqualTo(sessionUUID))
                    .and(ScreenshotDataRecordDynamicSqlSupport.timestamp, SqlBuilder.isLessThanOrEqualTo(at))
                    .build()
                    .execute();
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Long> getIdAt(final String sessionUUID, final Long at) {
        return Result.tryCatch(() -> {

            final List<Long> execute = SelectDSL
                    .selectWithMapper(this.screenshotDataRecordMapper::selectIds, id, timestamp)
                    .from(screenshotDataRecord)
                    .where(ScreenshotDataRecordDynamicSqlSupport.sessionUuid, SqlBuilder.isEqualTo(sessionUUID))
                    .and(ScreenshotDataRecordDynamicSqlSupport.timestamp, SqlBuilder.isLessThanOrEqualTo(at))
                    .orderBy(timestamp.descending())
                    .limit(1)
                    .build()
                    .execute();

            if (execute == null) {
                throw new NoResourceFoundException(EntityType.SCREENSHOT, sessionUUID);
            }

            return execute.get(0);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Long> getLatestImageId(final String sessionUUID) {
        return Result.tryCatch(() -> {

            final List<Long> execute = SelectDSL
                    .selectDistinctWithMapper(this.screenshotDataRecordMapper::selectIds, id, timestamp)
                    .from(screenshotDataRecord)
                    .where(ScreenshotDataRecordDynamicSqlSupport.sessionUuid, SqlBuilder.isEqualTo(sessionUUID))
                    .orderBy(timestamp.descending())
                    .limit(1)
                    .build()
                    .execute();

            return execute.get(0);
        });
    }

    @Override
    public Result<ScreenshotDataRecord> getLatest(final String sessionUUID) {
        return Result.tryCatch(() -> {
            return SelectDSL
                    .selectWithMapper(this.screenshotDataRecordMapper::selectOne,
                            id,
                            sessionUuid,
                            timestamp,
                            imageFormat,
                            metaData)
                    .from(screenshotDataRecord)
                    .where(ScreenshotDataRecordDynamicSqlSupport.sessionUuid, SqlBuilder.isEqualTo(sessionUUID))
                    .orderBy(timestamp.descending())
                    .limit(1)
                    .build()
                    .execute();
        });
    }

    @Override
    public Result<Map<String, ScreenshotDataRecord>> allLatestIn(final List<String> sessionUUIDs) {
        return Result.tryCatch(() -> {
            if (sessionUUIDs == null || sessionUUIDs.isEmpty()) {
                return Collections.emptyMap();
            }

            // NOTE: This was not working as expected since limit does not work with group (groupBy)
//            return SelectDSL
//                    .selectWithMapper(this.screenshotDataRecordMapper::selectMany,
//                            id,
//                            sessionUuid,
//                            timestamp,
//                            imageFormat,
//                            metaData)
//                    .from(screenshotDataRecord)
//                    .where(ScreenshotDataRecordDynamicSqlSupport.sessionUuid, SqlBuilder.isIn(sessionUUIDs))
//                    .groupBy(ScreenshotDataRecordDynamicSqlSupport.sessionUuid)
//                    .orderBy(timestamp.descending())
//                    .limit(1)
//                    .build()
//                    .execute()
//                    .stream()
//                    .collect(Collectors.toMap(r -> r.getSessionUuid(), Function.identity()));

            // NOTE: For now we use a less efficient version that uses getLatest(final String sessionUUID) for
            //       all requested sessions but in the future we should solve this problem on DB layer
            return sessionUUIDs.stream()
                    .map(this::getLatest)
                    .flatMap(Result::onErrorLogAndSkip)
                    .collect(Collectors.toMap(r -> r.getSessionUuid(), Function.identity()));
        });
    }

    @Override
    public Result<Collection<ScreenshotData>> allMatching(
            final FilterMap filterMap,
            final Predicate<ScreenshotData> predicate) {

        return Result.tryCatch(() -> {

            return this.screenshotDataRecordMapper
                    .selectByExample()
                    .where(
                            ScreenshotDataRecordDynamicSqlSupport.imageFormat,
                            SqlBuilder.isEqualToWhenPresent(
                                    filterMap.getInteger(Domain.SCREENSHOT_DATA.ATTR_IMAGE_FORMAT)))
                    .and(
                            ScreenshotDataRecordDynamicSqlSupport.metaData,
                            isLikeWhenPresent(filterMap.getSQLWildcard(Domain.SCREENSHOT_DATA.ATTR_META_DATA)))
                    .and(
                            ScreenshotDataRecordDynamicSqlSupport.timestamp,
                            SqlBuilder.isGreaterThanOrEqualToWhenPresent(
                                    filterMap.getLong(Domain.SCREENSHOT_DATA.ATTR_TIMESTAMP)))
                    .build()
                    .execute()
                    .stream()
                    .map(this::toDomainModel)
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional
    public Result<ScreenshotData> createNew(final ScreenshotData data) {
        return save(data);
    }

    @Override
    @Transactional
    public Result<ScreenshotData> save(final ScreenshotData data) {
        return save(data.sessionUUID, data.timestamp, data.imageFormat, data.metaData)
                .map(pk -> this.screenshotDataRecordMapper.selectByPrimaryKey(pk))
                .map(this::toDomainModel);
    }

    @Override
    @Transactional
    public Result<Long> save(
            final String sessionId,
            final Long timestamp,
            final ImageFormat imageFormat,
            final String metadata) {

        return Result.tryCatch(() -> {

            final ScreenshotDataRecord screenshotData = new ScreenshotDataRecord(
                    null,
                    sessionId,
                    timestamp,
                    (imageFormat != null) ? imageFormat.code : null,
                    metadata);

            this.screenshotDataRecordMapper.insert(screenshotData);
            return screenshotData.getId();
        });
    }

    @Override
    @Transactional
    public Result<Collection<EntityKey>> delete(final Set<EntityKey> all) {
        return Result.tryCatch(() -> {

            final List<Long> ids = extractListOfPKs(all);
            if (ids == null || ids.isEmpty()) {
                return Collections.emptyList();
            }

            // get all client access records for later processing
            final List<ScreenshotDataRecord> records = this.screenshotDataRecordMapper
                    .selectByExample()
                    .where(ScreenshotDataRecordDynamicSqlSupport.id, isIn(ids))
                    .build()
                    .execute();

            // then delete the client access
            this.screenshotDataRecordMapper
                    .deleteByExample()
                    .where(ScreenshotDataRecordDynamicSqlSupport.id, isIn(ids))
                    .build()
                    .execute();

            return records.stream()
                    .map(rec -> new EntityKey(rec.getId(), EntityType.SCREENSHOT_DATA))
                    .collect(Collectors.toList());
        });
    }

    private ScreenshotData toDomainModel(final ScreenshotDataRecord record) {
        return new ScreenshotData(
                record.getId(),
                record.getSessionUuid(),
                record.getTimestamp(),
                (record.getImageFormat() != null)
                        ? ImageFormat.valueOf(record.getImageFormat())
                        : null,
                record.getMetaData());
    }

}
