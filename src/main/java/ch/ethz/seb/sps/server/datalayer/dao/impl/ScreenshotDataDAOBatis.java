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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import ch.ethz.seb.sps.server.datalayer.batis.custommappers.ScreenshotDataMapper;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.select.MyBatis3SelectModelAdapter;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectDSL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.api.API.ScreenshotMetadataType;
import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.service.ScreenshotData;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordMapper;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.SessionRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataRecord;
import ch.ethz.seb.sps.server.datalayer.dao.NoResourceFoundException;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDataDAO;
import ch.ethz.seb.sps.utils.Constants;
import ch.ethz.seb.sps.utils.Result;

@Service
public class ScreenshotDataDAOBatis implements ScreenshotDataDAO {

    private final ScreenshotDataRecordMapper screenshotDataRecordMapper;

    private final ScreenshotDataMapper screenshotDataMapper;


    public ScreenshotDataDAOBatis(final ScreenshotDataRecordMapper screenshotDataRecordMapper, final ScreenshotDataMapper screenshotDataMapper) {
        this.screenshotDataRecordMapper = screenshotDataRecordMapper;
        this.screenshotDataMapper = screenshotDataMapper;
    }

    @Override
    public EntityType entityType() {
        return EntityType.SCREENSHOT_DATA;
    }

    @Override
    @Transactional(readOnly = true)
    public Result<ScreenshotData> byPK(final Long id) {
        return Result.tryCatch(() -> {
            final ScreenshotDataRecord record = this.screenshotDataRecordMapper
                    .selectByPrimaryKey(id);

            if (record == null) {
                throw new NoResourceFoundException(EntityType.SCREENSHOT_DATA, "For id: " + id);
            }

            return record;
        })
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
            ScreenshotDataRecord record = SelectDSL
                    .selectWithMapper(this.screenshotDataRecordMapper::selectOne,
                            id,
                            sessionUuid,
                            timestamp,
                            imageFormat,
                            metaData,
                            timestamp)
                    .from(screenshotDataRecord)
                    .where(ScreenshotDataRecordDynamicSqlSupport.sessionUuid, SqlBuilder.isEqualTo(sessionUUID))
                    .and(ScreenshotDataRecordDynamicSqlSupport.timestamp, SqlBuilder.isLessThanOrEqualTo(at))
                    .orderBy(timestamp.descending())
                    .limit(1)
                    .build()
                    .execute();

            if (record != null) {
                return record;
            }

            // there is no screenshot at the time of given timestamp. Try to get first image for the session
            record = SelectDSL
                    .selectWithMapper(this.screenshotDataRecordMapper::selectOne,
                            id,
                            sessionUuid,
                            timestamp,
                            imageFormat,
                            metaData,
                            timestamp)
                    .from(screenshotDataRecord)
                    .where(ScreenshotDataRecordDynamicSqlSupport.sessionUuid, SqlBuilder.isEqualTo(sessionUUID))
                    .orderBy(timestamp)
                    .limit(1)
                    .build()
                    .execute();

            // still no screenshot... seems that there are none at this time
            if (record == null) {
                throw new NoResourceFoundException(EntityType.SCREENSHOT, sessionUUID);
            }

            return record;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Long> getIdAt(final String sessionUUID, final Long at) {
        return Result.tryCatch(() -> {

            List<Long> result = SelectDSL
                    .selectWithMapper(this.screenshotDataRecordMapper::selectIds, id, timestamp)
                    .from(screenshotDataRecord)
                    .where(ScreenshotDataRecordDynamicSqlSupport.sessionUuid, SqlBuilder.isEqualTo(sessionUUID))
                    .and(ScreenshotDataRecordDynamicSqlSupport.timestamp, SqlBuilder.isLessThanOrEqualTo(at))
                    .orderBy(timestamp.descending())
                    .limit(1)
                    .build()
                    .execute();

            if (result != null && !result.isEmpty()) {
                return result.get(0);
            }

            // there is no screenshot at the time of given timestamp. Try to get first image for the session
            result = SelectDSL
                    .selectWithMapper(this.screenshotDataRecordMapper::selectIds, id, timestamp)
                    .from(screenshotDataRecord)
                    .where(ScreenshotDataRecordDynamicSqlSupport.sessionUuid, SqlBuilder.isEqualTo(sessionUUID))
                    .orderBy(timestamp)
                    .limit(1)
                    .build()
                    .execute();

            // still no screenshot... seems that there are none at this time
            if (result == null || result.isEmpty()) {
                throw new NoResourceFoundException(EntityType.SCREENSHOT, sessionUUID);
            }

            return result.get(0);
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
    @Transactional(readOnly = true)
    public Result<ScreenshotDataRecord> getLatest(final String sessionUUID) {
        return Result.tryCatch(() -> {
            final ScreenshotDataRecord latestScreenshotDataRec = getLatestScreenshotDataRec(sessionUUID);
            if (latestScreenshotDataRec == null) {
                throw new NoResourceFoundException(EntityType.SCREENSHOT_DATA, sessionUUID);
            }
            return latestScreenshotDataRec;
        });
    }

    @Override
    @Transactional(readOnly = true)
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
                    .map(this::getLatestScreenshotDataRec)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(r -> r.getSessionUuid(), Function.identity()));
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<ScreenshotData>> allMatching(
            final FilterMap filterMap,
            final Collection<Long> prePredicated) {

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
                    .and(
                            ScreenshotDataRecordDynamicSqlSupport.id,
                            SqlBuilder.isInWhenPresent((prePredicated == null)
                                    ? Collections.emptyList()
                                    : prePredicated))
                    .build()
                    .execute()
                    .stream()
                    .map(this::toDomainModel)
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Set<Long>> getAllOwnedIds(final String userUUID) {
        return Result.of(Collections.emptySet());
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<ScreenshotDataRecord>> searchScreenshotData(final FilterMap filterMap) {
        return Result.tryCatch(() -> {

            final String groupPKs = filterMap.getString(Domain.SESSION.ATTR_GROUP_ID);

            final String sessionUUID = filterMap.getString(API.PARAM_SESSION_ID);
            final String sessionUserName = filterMap.getSQLWildcard(Domain.SESSION.ATTR_CLIENT_NAME);
            final String machineName = filterMap.getSQLWildcard(Domain.SESSION.ATTR_CLIENT_MACHINE_NAME);
            final String osName = filterMap.getSQLWildcard(Domain.SESSION.ATTR_CLIENT_OS_NAME);
            final String sebVersion = filterMap.getSQLWildcard(Domain.SESSION.ATTR_CLIENT_VERSION);

            final Long fromTime = filterMap.getLong(API.PARAM_FROM_TIME);
            final Long toTime = filterMap.getLong(API.PARAM_TO_TIME);

            QueryExpressionDSL<MyBatis3SelectModelAdapter<List<ScreenshotDataRecord>>>.QueryExpressionWhereBuilder queryBuilder =
                    this.screenshotDataRecordMapper
                            .selectByExample()
                            .join(SessionRecordDynamicSqlSupport.sessionRecord)
                            .on(
                                    SessionRecordDynamicSqlSupport.uuid,
                                    SqlBuilder.equalTo(ScreenshotDataRecordDynamicSqlSupport.sessionUuid))

                            // session constraint
                            .where(SessionRecordDynamicSqlSupport.uuid, SqlBuilder.isEqualToWhenPresent(sessionUUID))

                            // session data constraint
                            .and(SessionRecordDynamicSqlSupport.clientName,
                                    SqlBuilder.isLikeWhenPresent(sessionUserName))
                            .and(SessionRecordDynamicSqlSupport.clientMachineName,
                                    SqlBuilder.isLikeWhenPresent(machineName))
                            .and(SessionRecordDynamicSqlSupport.clientOsName, SqlBuilder.isLikeWhenPresent(osName))
                            .and(SessionRecordDynamicSqlSupport.clientVersion, SqlBuilder.isLikeWhenPresent(sebVersion))

                            // time range constraint
                            .and(
                                    ScreenshotDataRecordDynamicSqlSupport.timestamp,
                                    SqlBuilder.isGreaterThanOrEqualToWhenPresent(fromTime))
                            .and(
                                    ScreenshotDataRecordDynamicSqlSupport.timestamp,
                                    SqlBuilder.isLessThanOrEqualToWhenPresent(toTime));

            // group constraint
            if (groupPKs != null) {
                if (groupPKs.contains(Constants.LIST_SEPARATOR)) {
                    final List<Long> pksAsList = Arrays.asList(StringUtils.split(groupPKs, Constants.LIST_SEPARATOR))
                            .stream()
                            .map(Long::parseLong)
                            .collect(Collectors.toList());
                    queryBuilder = queryBuilder.and(
                            SessionRecordDynamicSqlSupport.groupId,
                            SqlBuilder.isInWhenPresent(pksAsList));
                } else {
                    queryBuilder = queryBuilder.and(
                            SessionRecordDynamicSqlSupport.groupId,
                            SqlBuilder.isEqualToWhenPresent(Long.parseLong(groupPKs)));
                }
            }

            // meta data constraint
            final ScreenshotMetadataType[] metaData = API.ScreenshotMetadataType.values();
            for (int i = 0; i < metaData.length; i++) {
                final ScreenshotMetadataType mc = metaData[i];

                final String sqlWildcard = filterMap.getSQLWildcard(mc.parameterName);
                if (sqlWildcard == null) {
                    continue;
                }

                final String value =
                        Constants.PERCENTAGE_STRING +
                                Constants.DOUBLE_QUOTE +
                                mc.parameterName +
                                Constants.DOUBLE_QUOTE +
                                sqlWildcard;

                queryBuilder = queryBuilder.and(
                        ScreenshotDataRecordDynamicSqlSupport.metaData,
                        SqlBuilder.isLike(value));
            }

            return queryBuilder.build().execute();
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<Long>> getScreenshotTimestamps(final String sessionUUID){
        return Result.tryCatch(() -> {
            final List<Long> result = this.screenshotDataMapper
                    .selectScreenshotTimestamps(sessionUUID)
                    .build()
                    .execute();

           return result;
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

    private ScreenshotDataRecord getLatestScreenshotDataRec(final String sessionUUID) {
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
