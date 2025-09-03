/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao.impl;

import static ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;
import static org.mybatis.dynamic.sql.SqlBuilder.isLikeWhenPresent;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import ch.ethz.seb.sps.domain.model.PageSortOrder;
import ch.ethz.seb.sps.server.datalayer.batis.custommappers.ScreenshotDataMapper;
import ch.ethz.seb.sps.server.datalayer.batis.custommappers.SearchApplicationMapper;
import ch.ethz.seb.sps.domain.model.service.UserListForApplicationSearch;
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
    private final SearchApplicationMapper searchApplicationMapper;

    public ScreenshotDataDAOBatis(
            final ScreenshotDataRecordMapper screenshotDataRecordMapper,
            final ScreenshotDataMapper screenshotDataMapper,
            final SearchApplicationMapper searchApplicationMapper) {

        this.screenshotDataRecordMapper = screenshotDataRecordMapper;
        this.screenshotDataMapper = screenshotDataMapper;
        this.searchApplicationMapper = searchApplicationMapper;
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
    public Result<Collection<ScreenshotDataRecord>> allOfSession(final String sessionUUID) {
        return Result.tryCatch(() -> this.screenshotDataRecordMapper.selectByExample()
                .where(ScreenshotDataRecordDynamicSqlSupport.sessionUuid, SqlBuilder.isEqualTo(sessionUUID))
                .build()
                .execute());
    }

    @Override
    @Transactional(readOnly = true)
    public Result<ScreenshotDataRecord> getLatest(final String sessionUUID) {
        return Result.tryCatch(() -> {

            System.out.println("******************** getLatest called for session: " + sessionUUID );
            
            final ScreenshotDataRecord latestScreenshotDataRec = getLatestScreenshotDataRec(sessionUUID);
            if (latestScreenshotDataRec == null) {
                throw new NoResourceFoundException(EntityType.SCREENSHOT_DATA, sessionUUID);
            }
            return latestScreenshotDataRec;
        });
    }

    @Override
    public Result<Map<String, ScreenshotDataRecord>> allOfMappedToSession(final List<Long> pks) {
        return Result.tryCatch(() -> {

            if (pks == null || pks.isEmpty()) {
                return Collections.emptyMap();
            }
            
            // TODO only for testing remove this
            if (log.isDebugEnabled()) {
                log.debug("Get Screenshotdata for gallery view: {}", pks);
            }

            return screenshotDataRecordMapper
                    .selectByExample()
                    .where(id, isIn(pks))
                    .build()
                    .execute()
                    .stream()
                    .collect(Collectors.toMap(ScreenshotDataRecord::getSessionUuid, Function.identity()));
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
            final String sessionIpAddress = filterMap.getSQLWildcard(Domain.SESSION.ATTR_CLIENT_IP);
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
                            .and(SessionRecordDynamicSqlSupport.clientIp,
                                    SqlBuilder.isLikeWhenPresent(sessionIpAddress))
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
                    final List<Long> pksAsList = Arrays.stream(StringUtils.split(groupPKs, Constants.LIST_SEPARATOR))
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
            for (final ScreenshotMetadataType mc : metaData) {
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

            return queryBuilder
                    .build()
                    .execute();
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<List<Date>> selectMatchingScreenshotDataPerDay(FilterMap filterMap){

        final Long fromTime = filterMap.getLong(API.PARAM_FROM_TIME);
        final Long toTime = filterMap.getLong(API.PARAM_TO_TIME);

        return Result.tryCatch(() -> {
            QueryExpressionDSL<MyBatis3SelectModelAdapter<List<Date>>>.QueryExpressionWhereBuilder queryBuilder =
                    this.screenshotDataMapper
                            .selectTimestampsAsDates()
                            .where(
                                    ScreenshotDataRecordDynamicSqlSupport.timestamp,
                                    SqlBuilder.isGreaterThanOrEqualToWhenPresent(fromTime))
                            .and(
                                    ScreenshotDataRecordDynamicSqlSupport.timestamp,
                                    SqlBuilder.isLessThanOrEqualToWhenPresent(toTime));

            // meta data constraint
            final ScreenshotMetadataType[] metaData = API.ScreenshotMetadataType.values();
            for (final ScreenshotMetadataType mc : metaData) {
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

            return queryBuilder
                    .build()
                    .execute();
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<Long>> getScreenshotTimestamps(
            final String sessionUUID,
            final Long timestamp,
            final PageSortOrder sortOrder) {

        return Result.tryCatch(() -> {
            final List<Long> result = this.screenshotDataMapper
                    .selectScreenshotTimestamps(sessionUUID, timestamp, sortOrder)
                    .build()
                    .execute();

            if(sortOrder.equals(PageSortOrder.DESCENDING)){
                Collections.reverse(result);
            }

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
                .map(this.screenshotDataRecordMapper::selectByPrimaryKey)
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


    @Override
    @Transactional(readOnly = true)
    public Result<Collection<String>> getDistinctMetadataAppForExam(final List<Long> groupIds) {
        return Result.tryCatch(() -> {

            QueryExpressionDSL<MyBatis3SelectModelAdapter<List<String>>>.QueryExpressionWhereBuilder queryBuilder =
                    this.searchApplicationMapper
                            .selectDistinctMetadataAppForExam()
                            .join(
                                    SessionRecordDynamicSqlSupport.sessionRecord
                            )
                            .on(
                                    ScreenshotDataRecordDynamicSqlSupport.sessionUuid,
                                    SqlBuilder.equalTo(SessionRecordDynamicSqlSupport.uuid))
                            .where(
                                    SessionRecordDynamicSqlSupport.groupId,
                                    isIn(groupIds)
                            );

            return queryBuilder
                    .build()
                    .execute();
        });
    }


    @Override
    @Transactional(readOnly = true)
    public Result<Collection<String>> getDistinctMetadataWindowForExam(final String metadataApplication, final List<Long> groupIds) {
        return Result.tryCatch(() -> {

            final String metadataAppValue = createMetadataSearchString(
                    API.SCREENSHOT_META_DATA_APPLICATION, 
                    metadataApplication);

            QueryExpressionDSL<MyBatis3SelectModelAdapter<List<String>>>.QueryExpressionWhereBuilder queryBuilder =
                    this.searchApplicationMapper
                            .selectDistinctWindowTitle()
                            .join(
                                    SessionRecordDynamicSqlSupport.sessionRecord
                            )
                            .on(
                                    ScreenshotDataRecordDynamicSqlSupport.sessionUuid,
                                    SqlBuilder.equalTo(SessionRecordDynamicSqlSupport.uuid))
                            .where(
                                    ScreenshotDataRecordDynamicSqlSupport.metaData,
                                    SqlBuilder.isLike(metadataAppValue)
                            )
                            .and(
                                    SessionRecordDynamicSqlSupport.groupId,
                                    isIn(groupIds)
                            );

            return queryBuilder
                    .build()
                    .execute();
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Long> countDistinctMetadataWindowForExam(final String metadataApplication, final List<Long> groupIds) {
        return Result.tryCatch(() -> {

            final String metadataAppValue = createMetadataSearchString(API.SCREENSHOT_META_DATA_APPLICATION, metadataApplication);

            QueryExpressionDSL<MyBatis3SelectModelAdapter<Long>>.QueryExpressionWhereBuilder queryBuilder =
                    this.screenshotDataRecordMapper
                            .countByExample()
                            .join(
                                    SessionRecordDynamicSqlSupport.sessionRecord
                            )
                            .on(
                                    ScreenshotDataRecordDynamicSqlSupport.sessionUuid,
                                    SqlBuilder.equalTo(SessionRecordDynamicSqlSupport.uuid))
                            .where(
                                    metaData,
                                    SqlBuilder.isLike(metadataAppValue)
                            ).and(
                                    SessionRecordDynamicSqlSupport.groupId,
                                    isIn(groupIds)
                            );

            return queryBuilder
                    .build()
                    .execute();
        });
    }


    @Override
    @Transactional(readOnly = true)
    public Result<List<UserListForApplicationSearch>> getUserListForApplicationSearch(
            final String metadataApplication, 
            final String metadataWindowTitle, 
            final List<Long> groupIds) {
        
        return Result.tryCatch(() -> {

            final String metadataAppValue = createMetadataSearchString(
                    API.SCREENSHOT_META_DATA_APPLICATION, 
                    metadataApplication);
            final String metadataWindowValue = createMetadataSearchString(
                    API.SCREENSHOT_META_DATA_ACTIVE_WINDOW_TITLE, 
                    metadataWindowTitle);

            QueryExpressionDSL<MyBatis3SelectModelAdapter<List<UserListForApplicationSearch>>>.QueryExpressionWhereBuilder queryBuilder =
                    this.searchApplicationMapper
                            .selectUserListForApplicationSearch()
                            .join(
                                    SessionRecordDynamicSqlSupport.sessionRecord
                            )
                            .on(
                                    sessionUuid,
                                    SqlBuilder.equalTo(SessionRecordDynamicSqlSupport.uuid))
                            .where(
                                    metaData,
                                    SqlBuilder.isLike(metadataAppValue)
                            ).and(
                                    metaData,
                                    SqlBuilder.isLike(metadataWindowValue)
                            ).and(
                                    SessionRecordDynamicSqlSupport.groupId,
                                    isIn(groupIds)
                            );

            return queryBuilder
                    .build()
                    .execute();
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<List<Long>> getTimestampListForApplicationSearch(
            final String sessionUuid, 
            final String metadataApplication, 
            final String metadataWindowTitle) {
        
        return Result.tryCatch(() -> {

            final String metadataAppValue = createMetadataSearchString(
                    API.SCREENSHOT_META_DATA_APPLICATION, 
                    metadataApplication);
            final String metadataWindowValue = createMetadataSearchString(
                    API.SCREENSHOT_META_DATA_ACTIVE_WINDOW_TITLE, 
                    metadataWindowTitle);

            QueryExpressionDSL<MyBatis3SelectModelAdapter<List<Long>>>.QueryExpressionWhereBuilder queryBuilder =
                    this.searchApplicationMapper
                            .selectTimestampListForApplicationSearch()
                            .where(
                                    ScreenshotDataRecordDynamicSqlSupport.sessionUuid,
                                    SqlBuilder.isEqualTo(sessionUuid)
                            )
                            .and(
                                    ScreenshotDataRecordDynamicSqlSupport.metaData,
                                    SqlBuilder.isLike(metadataAppValue)
                            )
                            .and(
                                    ScreenshotDataRecordDynamicSqlSupport.metaData,
                                    SqlBuilder.isLike(metadataWindowValue)
                            );

            return queryBuilder
                    .build()
                    .execute();
        });
    }

    @Override
    public Result<ScreenshotDataRecord> recordByPK(Long pk) {
        return Result.tryCatch(() ->  this.screenshotDataRecordMapper.selectByPrimaryKey(pk));
    }

    private ScreenshotDataRecord getLatestScreenshotDataRec(final String sessionUUID) {

        System.out.println("******************** getLatestScreenshotDataRec called for session: " + sessionUUID );
        
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

    private String createMetadataSearchString(String metadataKey, String metadataValue){
        return  Constants.PERCENTAGE_STRING +
                Constants.DOUBLE_QUOTE +
                metadataKey +
                Constants.DOUBLE_QUOTE +
                Constants.PERCENTAGE_STRING +
                metadataValue +
                Constants.PERCENTAGE_STRING;
    }

}
