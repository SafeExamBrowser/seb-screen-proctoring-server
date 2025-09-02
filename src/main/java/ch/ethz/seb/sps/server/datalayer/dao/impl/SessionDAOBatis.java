/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao.impl;

import static ch.ethz.seb.sps.server.datalayer.batis.mapper.GroupRecordDynamicSqlSupport.id;
import static ch.ethz.seb.sps.server.datalayer.batis.mapper.GroupRecordDynamicSqlSupport.lastUpdateTime;
import static ch.ethz.seb.sps.server.datalayer.batis.mapper.GroupRecordDynamicSqlSupport.terminationTime;
import static ch.ethz.seb.sps.server.datalayer.batis.mapper.SessionRecordDynamicSqlSupport.*;
import static ch.ethz.seb.sps.server.datalayer.batis.mapper.SessionRecordDynamicSqlSupport.uuid;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

import ch.ethz.seb.sps.server.datalayer.batis.custommappers.SearchSessionMapper;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.*;
import ch.ethz.seb.sps.utils.Cryptor;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.select.MyBatis3SelectModelAdapter;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.api.API.ScreenshotMetadataType;
import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.service.Session;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import ch.ethz.seb.sps.server.datalayer.batis.model.SessionRecord;
import ch.ethz.seb.sps.server.datalayer.dao.DuplicateEntityException;
import ch.ethz.seb.sps.server.datalayer.dao.NoResourceFoundException;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDAO;
import ch.ethz.seb.sps.server.datalayer.dao.SessionDAO;
import ch.ethz.seb.sps.server.weblayer.BadRequestException;
import ch.ethz.seb.sps.utils.Constants;
import ch.ethz.seb.sps.utils.Result;
import ch.ethz.seb.sps.utils.Utils;

@Service
public class SessionDAOBatis implements SessionDAO {

    private final SearchSessionMapper searchSessionMapper;
    private final SessionRecordMapper sessionRecordMapper;
    private final GroupRecordMapper groupRecordMapper;
    private final ScreenshotDataRecordMapper screenshotDataRecordMapper;
    private final ScreenshotDAO screenshotDAO;
    private final Cryptor cryptor;

    public SessionDAOBatis(
            final SearchSessionMapper searchSessionMapper,
            final SessionRecordMapper sessionRecordMapper,
            final GroupRecordMapper groupRecordMapper,
            final ScreenshotDataRecordMapper screenshotDataRecordMapper,
            final ScreenshotDAO screenshotDAO, 
            final Cryptor cryptor) {

        this.searchSessionMapper = searchSessionMapper;
        this.sessionRecordMapper = sessionRecordMapper;
        this.groupRecordMapper = groupRecordMapper;
        this.screenshotDataRecordMapper = screenshotDataRecordMapper;
        this.screenshotDAO = screenshotDAO;
        this.cryptor = cryptor;
    }

    @Override
    public EntityType entityType() {
        return EntityType.SESSION;
    }

    @Override
    public Long modelIdToPK(final String modelId) {
        final Long pk = isPK(modelId);
        if (pk != null) {
            return pk;
        } else {
            return pkByUUID(modelId).getOr(null);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Session> byPK(final Long id) {
        return recordByPK(id)
                .map(this::toDomainModel);
    }

    @Override
    public Result<Session> byModelId(final String id) {
        try {
            final long pk = Long.parseLong(id);
            return this.byPK(pk);
        } catch (final Exception e) {
            return recordByUUID(id)
                    .map(this::toDomainModel);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<Session>> allOf(final Set<Long> pks) {
        return Result.tryCatch(() -> {

            if (pks == null || pks.isEmpty()) {
                return Collections.emptyList();
            }

            return this.sessionRecordMapper
                    .selectByExample()
                    .where(SessionRecordDynamicSqlSupport.id, isIn(new ArrayList<>(pks)))
                    .build()
                    .execute()
                    .stream()
                    .map(this::toDomainModel)
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<String>> allLiveSessionUUIDsByGroupId(final Long groupId) {
        return Result.tryCatch(() -> {
            return this.sessionRecordMapper.selectByExample()
                    .where(SessionRecordDynamicSqlSupport.groupId, SqlBuilder.isEqualTo(groupId))
                    .and(SessionRecordDynamicSqlSupport.terminationTime, SqlBuilder.isNull())
                    .build()
                    .execute()
                    .stream()
                    .map(SessionRecord::getUuid)
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<String>> allSessionUUIDsByGroupId(final Long groupId) {
        return Result.tryCatch(() -> {
            return this.sessionRecordMapper.selectByExample()
                    .where(SessionRecordDynamicSqlSupport.groupId, SqlBuilder.isEqualTo(groupId))
                    .build()
                    .execute()
                    .stream()
                    .map(SessionRecord::getUuid)
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Long> allLiveSessionCount(final Long groupId) {
        return Result.tryCatch(() -> this.sessionRecordMapper
                .countByExample()
                .where(SessionRecordDynamicSqlSupport.groupId, SqlBuilder.isEqualTo(groupId))
                .and(SessionRecordDynamicSqlSupport.terminationTime, SqlBuilder.isNull())
                .build()
                .execute());
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Long> allSessionCount(final Long groupId) {
        return Result.tryCatch(() -> {
            return this.sessionRecordMapper.countByExample()
                    .where(SessionRecordDynamicSqlSupport.groupId, SqlBuilder.isEqualTo(groupId))
                    .build()
                    .execute();
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<List<Date>> queryMatchingDaysForSessionSearch(final FilterMap filterMap) {
        return Result.tryCatch(() -> {

            final Boolean active = filterMap.getBooleanObject(API.ACTIVE_FILTER);
            final Long fromTime = filterMap.getLong(API.PARAM_FROM_TIME);
            final Long toTime = filterMap.getLong(API.PARAM_TO_TIME);

            final String groupPKs = filterMap.getString(Domain.SESSION.ATTR_GROUP_ID);
            final String sessionUUID = filterMap.contains(API.PARAM_SESSION_ID)
                    ? filterMap.getString(API.PARAM_SESSION_ID)
                    : filterMap.getString(Domain.SESSION.ATTR_UUID);

            QueryExpressionDSL<MyBatis3SelectModelAdapter<List<Date>>>.QueryExpressionWhereBuilder queryBuilder =
                    this.searchSessionMapper
                            .selectCreationTimesAsDates()
                            .where(
                                    SessionRecordDynamicSqlSupport.terminationTime,
                                    (active != null) ? active ? SqlBuilder.isNull() : SqlBuilder.isNotNull()
                                            : SqlBuilder.isEqualToWhenPresent(() -> null))
                            .and(
                                    SessionRecordDynamicSqlSupport.uuid,
                                    SqlBuilder.isEqualToWhenPresent(sessionUUID))
                            .and(
                                    SessionRecordDynamicSqlSupport.clientName,
                                    isLikeWhenPresent(filterMap.getSQLWildcard(Domain.SESSION.ATTR_CLIENT_NAME)))
                            .and(
                                    SessionRecordDynamicSqlSupport.clientMachineName,
                                    isLikeWhenPresent(
                                            filterMap.getSQLWildcard(Domain.SESSION.ATTR_CLIENT_MACHINE_NAME)))
                            .and(
                                    SessionRecordDynamicSqlSupport.clientVersion,
                                    isLikeWhenPresent(filterMap.getSQLWildcard(Domain.SESSION.ATTR_CLIENT_VERSION)))
                            .and(
                                    SessionRecordDynamicSqlSupport.clientIp,
                                    isLikeWhenPresent(filterMap.getSQLWildcard(Domain.SESSION.ATTR_CLIENT_IP)))
                            .and(
                                    SessionRecordDynamicSqlSupport.creationTime,
                                    SqlBuilder.isGreaterThanOrEqualToWhenPresent(fromTime))
                            .and(
                                    SessionRecordDynamicSqlSupport.creationTime,
                                    SqlBuilder.isLessThanOrEqualToWhenPresent(toTime));

            // group constraint
            if (groupPKs != null) {
                if (groupPKs.contains(Constants.LIST_SEPARATOR)) {
                    final List<Long> pksAsList = Arrays.
                            stream(StringUtils.split(groupPKs, Constants.LIST_SEPARATOR))
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

            return queryBuilder
                    .orderBy(SessionRecordDynamicSqlSupport.creationTime.descending())
                    .build()
                    .execute();
        });
    }
    
    @Override
    public Result<List<String>> allTokensThatNeedsUpdate(Long groupId, Map<String, Long> updateTimes) {
        return Result.tryCatch(() -> {
            
            
            if (updateTimes == null || updateTimes.isEmpty()) {
                return Collections.emptyList();
            }

            List<Long> idsForUpdate = this.sessionRecordMapper
                    .selectIdsByExample()
                    .where(SessionRecordDynamicSqlSupport.groupId, isEqualTo(groupId))
                    .and(uuid, isIn(updateTimes.keySet()))
                    .and(SessionRecordDynamicSqlSupport.lastUpdateTime, isNotIn(updateTimes.values()))
                    .build()
                    .execute();

            if (idsForUpdate != null && !idsForUpdate.isEmpty() && log.isDebugEnabled()) {
                log.debug("Found {} session tokens to refresh on group {}", idsForUpdate.size(), groupId);
            }

            if (idsForUpdate != null && !idsForUpdate.isEmpty()) {
                return sessionRecordMapper.selectByExample()
                        .where(SessionRecordDynamicSqlSupport.id, isIn(idsForUpdate))
                        .build()
                        .execute()
                        .stream()
                        .map(SessionRecord::getUuid)
                        .collect(Collectors.toList());
            }

            return Collections.emptyList();
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<String> getEncryptionKey(String uuid) {
        return recordByUUID(uuid)
                .flatMap(rec -> cryptor.decrypt(rec.getEncryptionKey()))
                .map(CharSequence::toString);
    }
  

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<Session>> allMatching(
            final FilterMap filterMap,
            final Collection<Long> prePredicated) {

        return Result.tryCatch(() -> {

            final Boolean active = filterMap.getBooleanObject(API.ACTIVE_FILTER);
            final Long fromTime = filterMap.getLong(API.PARAM_FROM_TIME);
            final Long toTime = filterMap.getLong(API.PARAM_TO_TIME);

            final String groupPKs = filterMap.getString(Domain.SESSION.ATTR_GROUP_ID);
            final String sessionUUID = filterMap.contains(API.PARAM_SESSION_ID)
                    ? filterMap.getString(API.PARAM_SESSION_ID)
                    : filterMap.getString(Domain.SESSION.ATTR_UUID);

            QueryExpressionDSL<MyBatis3SelectModelAdapter<List<SessionRecord>>>.QueryExpressionWhereBuilder queryBuilder =
                    this.sessionRecordMapper
                            .selectByExample()
                            .where(
                                    SessionRecordDynamicSqlSupport.terminationTime,
                                    (active != null) ? active ? SqlBuilder.isNull() : SqlBuilder.isNotNull()
                                            : SqlBuilder.isEqualToWhenPresent(() -> null))
                            .and(
                                    SessionRecordDynamicSqlSupport.uuid,
                                    SqlBuilder.isEqualToWhenPresent(sessionUUID))
                            .and(
                                    SessionRecordDynamicSqlSupport.clientName,
                                    isLikeWhenPresent(filterMap.getSQLWildcard(Domain.SESSION.ATTR_CLIENT_NAME)))
                            .and(
                                    SessionRecordDynamicSqlSupport.clientMachineName,
                                    isLikeWhenPresent(
                                            filterMap.getSQLWildcard(Domain.SESSION.ATTR_CLIENT_MACHINE_NAME)))
                            .and(
                                    SessionRecordDynamicSqlSupport.clientVersion,
                                    isLikeWhenPresent(filterMap.getSQLWildcard(Domain.SESSION.ATTR_CLIENT_VERSION)))
                            .and(
                                    SessionRecordDynamicSqlSupport.clientIp,
                                    isLikeWhenPresent(filterMap.getSQLWildcard(Domain.SESSION.ATTR_CLIENT_IP)))
                            .and(
                                    SessionRecordDynamicSqlSupport.creationTime,
                                    SqlBuilder.isGreaterThanOrEqualToWhenPresent(fromTime))
                            .and(
                                    SessionRecordDynamicSqlSupport.creationTime,
                                    SqlBuilder.isLessThanOrEqualToWhenPresent(toTime))
                            .and(
                                    SessionRecordDynamicSqlSupport.id,
                                    SqlBuilder.isInWhenPresent((prePredicated == null)
                                            ? Collections.emptyList()
                                            : prePredicated));

            // group constraint
            if (groupPKs != null) {
                if (groupPKs.contains(Constants.LIST_SEPARATOR)) {
                    final List<Long> pksAsList = Arrays
                            .stream(StringUtils.split(groupPKs, Constants.LIST_SEPARATOR))
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

            return queryBuilder
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
    @Transactional
    public Result<Session> createNew(final Session data) {
        return Result.tryCatch(() -> {

            checkUniqueUUID(data.uuid);
            
            final long now = Utils.getMillisecondsNow();
            final SessionRecord record = new SessionRecord(
                    null,
                    data.groupId,
                    (StringUtils.isNotBlank(data.uuid)) ? data.uuid : UUID.randomUUID().toString(),
                    data.imageFormat.code,
                    data.clientName,
                    data.clientIP,
                    data.clientMachineName,
                    data.clientOSName,
                    data.clientVersion,
                    now, 
                    now, 
                    null,
                    generateEncryptedSecret().toString());

            this.sessionRecordMapper.insert(record);
            return record.getId();
        })
                .flatMap(this::byPK)
                .onError(TransactionHandler::rollback);
    }

    @Override
    @Transactional
    public Result<Session> createNew(
            final String groupId,
            final String uuid,
            final String userSessionName,
            final String clientIP,
            final String clientMachineName,
            final String clientOSName,
            final String clientVersion,
            final ImageFormat imageFormat) {

        return Result.tryCatch(() -> {

            checkUniqueUUID(uuid);

            Long groupPK = isPK(groupId);
            if (groupPK == null) {
                final List<Long> groupPKs = this.groupRecordMapper.selectIdsByExample()
                        .where(GroupRecordDynamicSqlSupport.uuid, SqlBuilder.isEqualTo(groupId))
                        .build()
                        .execute();
                if (groupPKs == null || groupPKs.isEmpty()) {
                    throw new BadRequestException("create new session", "no group with modelId: " + groupId + " found");
                }
                groupPK = groupPKs.get(0);
            }

            final long now = Utils.getMillisecondsNow();
            final SessionRecord record = new SessionRecord(
                    null,
                    groupPK,
                    (StringUtils.isNotBlank(uuid)) ? uuid : UUID.randomUUID().toString(),
                    (imageFormat != null) ? imageFormat.code : ImageFormat.PNG.code,
                    userSessionName,
                    clientIP,
                    clientMachineName,
                    clientOSName,
                    clientVersion,
                    now, 
                    now, 
                    null,
                    generateEncryptedSecret().toString());

            this.sessionRecordMapper.insert(record);
            return record.getId();

        })
                .flatMap(this::byPK)
                .onError(TransactionHandler::rollback);
    }

    @Override
    @Transactional
    public Result<Collection<EntityKey>> delete(final Set<EntityKey> all) {
        return Result.tryCatch(() -> extractListOfPKs(all))
                .map(this::delete);
    }

    @Override
    @Transactional
    public Result<Collection<EntityKey>> deleteAllForGroups(final List<Long> groupPKs) {
        if (groupPKs == null || groupPKs.isEmpty()) {
            return Result.of(Collections.emptyList());
        }
        
        return Result.tryCatch(() -> this.sessionRecordMapper.selectIdsByExample()
                .where(SessionRecordDynamicSqlSupport.groupId, isIn(groupPKs))
                .build()
                .execute())
                .map(this::delete);
    }

    private Collection<EntityKey> delete(final List<Long> pks) {
        if (pks == null || pks.isEmpty()) {
            return Collections.emptyList();
        }

        final List<SessionRecord> sessions = this.sessionRecordMapper
                .selectByExample()
                .where(SessionRecordDynamicSqlSupport.id, isIn(pks))
                .build()
                .execute();

        // delete session data for each session
        sessions.forEach(this::deleteSessionScreenshots);

        this.sessionRecordMapper
                .deleteByExample()
                .where(SessionRecordDynamicSqlSupport.id, isIn(pks))
                .build()
                .execute();

        return sessions.stream()
                .map(rec -> new EntityKey(rec.getId(), EntityType.SESSION))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Result<Collection<EntityKey>> closeAllSessionsForGroup(final Long groupPK) {
        return Result.tryCatch(() -> {

            final List<Long> pks = this.sessionRecordMapper
                    .selectIdsByExample()
                    .where(SessionRecordDynamicSqlSupport.groupId, isEqualTo(groupPK))
                    .and(SessionRecordDynamicSqlSupport.terminationTime, isNull())
                    .build()
                    .execute();

            if (pks == null || pks.isEmpty()) {
                return Collections.emptyList();
            }

            final long now = Utils.getMillisecondsNow();

            UpdateDSL.updateWithMapper(this.sessionRecordMapper::update, sessionRecord)
                    .set(lastUpdateTime).equalTo(now)
                    .set(terminationTime).equalTo(now)
                    .where(id, isIn(pks))
                    .build()
                    .execute();

            return pks.stream()
                    .map(pk -> new EntityKey(pk, EntityType.SESSION))
                    .collect(Collectors.toList());
        });
    }

    private void deleteSessionScreenshots(final SessionRecord sessionRecord) {

        // get all screenshot record ids for the session
        final List<Long> screenShotPKs = this.screenshotDataRecordMapper
                .selectIdsByExample()
                .where(ScreenshotDataRecordDynamicSqlSupport.sessionUuid, isEqualTo(sessionRecord.getUuid()))
                .build()
                .execute();

        if (screenShotPKs == null || screenShotPKs.isEmpty()) {
            log.info("No session data found for deletion for session: {}", sessionRecord.getUuid());
            return;
        }

        // delete all screenshot data
        this.screenshotDataRecordMapper
                .deleteByExample()
                .where(ScreenshotDataRecordDynamicSqlSupport.id, isIn(screenShotPKs))
                .build()
                .execute();

        // then all screenshots
        this.screenshotDAO
                .deleteAllForSession(sessionRecord.getUuid(), screenShotPKs)
                .onError(error -> log.error("Failed to delete Screenshots: {}", screenShotPKs, error ));
    }

    @Override
    @Transactional
    public Result<Session> save(final Session data) {
        return Result.tryCatch(() -> {

            final long now = Utils.getMillisecondsNow();

            Long pk = data.id;
            if (pk == null && data.uuid != null) {
                pk = this.pkByUUID(data.uuid).getOr(null);
            }
            if (pk == null) {
                throw new BadRequestException("session save", "no session with uuid: " + data.uuid + "found");
            }

            UpdateDSL.updateWithMapper(this.sessionRecordMapper::update, sessionRecord)
                    .set(imageFormat).equalTo(data.imageFormat.code)
                    .set(clientName).equalTo(data.clientName)
                    .set(clientIp).equalTo(data.clientIP)
                    .set(clientMachineName).equalTo(data.clientMachineName)
                    .set(clientOsName).equalTo(data.clientOSName)
                    .set(clientVersion).equalTo(data.clientVersion)
                    .set(lastUpdateTime).equalTo(now)
                    .where(id, isEqualTo(pk))
                    .build()
                    .execute();

            return this.sessionRecordMapper.selectByPrimaryKey(pk);
        })
                .map(this::toDomainModel)
                .onError(TransactionHandler::rollback);
    }

    @Override
    @Transactional
    public Result<EntityKey> setActive(EntityKey entityKey, boolean active) {
        return pkByUUID(entityKey.modelId)
                .map(pk -> {

                    final long now = Utils.getMillisecondsNow();

                    UpdateDSL.updateWithMapper(this.sessionRecordMapper::update, sessionRecord)
                            .set(lastUpdateTime).equalTo(now)
                            .set(terminationTime).equalTo(() -> active ? null : now)
                            .where(id, isEqualTo(pk))
                            .build()
                            .execute();

                    return entityKey;
                });
    }

    @Override
    @Transactional
    public Result<Long> closeAt(String sessionUUID, Long termination_time) {
        return pkByUUID(sessionUUID)
                .map(pk -> {
        
                    final long now = Utils.getMillisecondsNow();
        
                    UpdateDSL.updateWithMapper(this.sessionRecordMapper::update, sessionRecord)
                            .set(lastUpdateTime).equalTo(now)
                            .set(terminationTime).equalTo(() -> termination_time)
                            .where(id, isEqualTo(pk))
                            .build()
                            .execute();
        
                    return pk;
                });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isActive(String modelId) {
        if (StringUtils.isBlank(modelId)) {
            return false;
        }

        return this.sessionRecordMapper
                .countByExample()
                .where(ExamRecordDynamicSqlSupport.id, isEqualTo(Long.valueOf(modelId)))
                .and(ExamRecordDynamicSqlSupport.terminationTime, SqlBuilder.isNull())
                .build()
                .execute() > 0;
    }

    @Override
    @Transactional
    public Result<String> closeSession(final String sessionUUID) {
        return Result.tryCatch(() -> {

            final long now = Utils.getMillisecondsNow();

            UpdateDSL.updateWithMapper(this.sessionRecordMapper::update, sessionRecord)
                    .set(lastUpdateTime).equalTo(now)
                    .set(terminationTime).equalTo(now)
                    .where(uuid, isEqualTo(sessionUUID))
                    .build()
                    .execute();

            return sessionUUID;
        })
                .onError(TransactionHandler::rollback);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNumberOfScreenshots(final String uuid, final FilterMap filterMap) {

        QueryExpressionDSL<MyBatis3SelectModelAdapter<Long>>.QueryExpressionWhereBuilder queryBuilder =
                this.screenshotDataRecordMapper
                        .countByExample()
                        .where(ScreenshotDataRecordDynamicSqlSupport.sessionUuid, isEqualTo(uuid));

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
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Boolean> hasAnySessionData(Collection<Long> groupIds) {
        return Result.tryCatch(() -> {

            List<Long> sessionIds = this.sessionRecordMapper
                    .selectIdsByExample()
                    .where(groupId, isIn(groupIds))
                    .build()
                    .execute();

            if (sessionIds == null  || sessionIds.isEmpty()) {
                return false;
            }

            Long dataNum = this.screenshotDataRecordMapper
                    .countByExample()
                    .where(ScreenshotDataRecordDynamicSqlSupport.id, isIn(sessionIds))
                    .build()
                    .execute();

            return dataNum != null && dataNum.intValue() != 0;
        });
    }

    private Result<SessionRecord> recordByPK(final Long pk) {
        return Result.tryCatch(() -> {

            final SessionRecord selectByPrimaryKey = this.sessionRecordMapper.selectByPrimaryKey(pk);

            if (selectByPrimaryKey == null) {
                throw new NoResourceFoundException(EntityType.SESSION, String.valueOf(pk));
            }

            return selectByPrimaryKey;
        });
    }

    private Result<SessionRecord> recordByUUID(final String uuid) {
        return Result.tryCatch(() -> {

            final List<SessionRecord> execute = this.sessionRecordMapper.selectByExample()
                    .where(SessionRecordDynamicSqlSupport.uuid, SqlBuilder.isEqualTo(uuid))
                    .build()
                    .execute();

            if (execute == null || execute.isEmpty()) {
                throw new NoResourceFoundException(EntityType.SESSION, uuid);
            }

            return execute.get(0);
        });
    }

    private Result<Long> pkByUUID(final String uuid) {
        return Result.tryCatch(() -> {

            final List<Long> execute = this.sessionRecordMapper
                    .selectIdsByExample()
                    .where(SessionRecordDynamicSqlSupport.uuid, SqlBuilder.isEqualTo(uuid))
                    .build()
                    .execute();

            if (execute == null || execute.isEmpty()) {
                throw new NoResourceFoundException(EntityType.SESSION, uuid);
            }

            return execute.get(0);
        });
    }

    private Session toDomainModel(final SessionRecord record) {
        return new Session(
                record.getId(),
                record.getGroupId(),
                record.getUuid(),
                record.getClientName(),
                record.getClientIp(),
                record.getClientMachineName(),
                record.getClientOsName(),
                record.getClientVersion(),
                (record.getImageFormat() != null)
                        ? ImageFormat.valueOf(record.getImageFormat())
                        : ImageFormat.PNG,
                record.getCreationTime(),
                record.getLastUpdateTime(),
                record.getTerminationTime());
    }

    private void checkUniqueUUID(final String uuid) {
        if (uuid != null) {
            final Long count = this.sessionRecordMapper.countByExample()
                    .where(SessionRecordDynamicSqlSupport.uuid, SqlBuilder.isEqualTo(uuid))
                    .build()
                    .execute();

            if (count != null && count > 0) {
                throw new DuplicateEntityException(EntityType.SESSION, Domain.SESSION.ATTR_UUID,
                        "UUID exists already");
            }
        }
    }

    private CharSequence generateEncryptedSecret() {
        final String secret = UUID.randomUUID().toString().replace("-", StringUtils.EMPTY);
        return this.cryptor
                .encrypt(secret)
                .getOrThrow();
    }
}
