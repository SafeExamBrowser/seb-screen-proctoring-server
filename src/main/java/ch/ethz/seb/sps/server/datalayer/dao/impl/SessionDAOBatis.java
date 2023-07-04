/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao.impl;

import static org.mybatis.dynamic.sql.SqlBuilder.isIn;
import static org.mybatis.dynamic.sql.SqlBuilder.isLikeWhenPresent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.javassist.NotFoundException;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.service.Session;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.GroupRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.SessionRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.SessionRecordMapper;
import ch.ethz.seb.sps.server.datalayer.batis.model.SessionRecord;
import ch.ethz.seb.sps.server.datalayer.dao.SessionDAO;
import ch.ethz.seb.sps.utils.Result;
import ch.ethz.seb.sps.utils.Utils;

@Service
public class SessionDAOBatis implements SessionDAO {

    private static final Logger log = LoggerFactory.getLogger(SessionDAOBatis.class);

    private final SessionRecordMapper sessionRecordMapper;

    public SessionDAOBatis(final SessionRecordMapper sessionRecordMapper) {
        this.sessionRecordMapper = sessionRecordMapper;
    }

    @Override
    public EntityType entityType() {
        return EntityType.SESSION;
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Session> byPK(final Long id) {
        return Result.tryCatch(() -> this.sessionRecordMapper
                .selectByPrimaryKey(id))
                .map(this::toDomainModel);
    }

    @Override
    public Result<Session> byModelId(final String id) {
        try {
            return this.byPK(Long.parseLong(id));
        } catch (final Exception e) {
            return byUUID(id);
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
    public Result<Collection<String>> allActiveSessionIds(final String groupUUID) {
        return Result.tryCatch(() -> {
            return this.sessionRecordMapper.selectByExample()
                    .leftJoin(GroupRecordDynamicSqlSupport.groupRecord)
                    .on(GroupRecordDynamicSqlSupport.id, SqlBuilder.equalTo(SessionRecordDynamicSqlSupport.groupId))
                    .where(GroupRecordDynamicSqlSupport.uuid, SqlBuilder.isEqualTo(groupUUID))
                    .and(SessionRecordDynamicSqlSupport.terminationTime, SqlBuilder.isNull())
                    .build()
                    .execute()
                    .stream()
                    .map(rec -> rec.getUuid())
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Session> byUUID(final String sessionUUID) {
        return Result.tryCatch(() -> {
            final List<SessionRecord> execute = this.sessionRecordMapper.selectByExample()
                    .where(SessionRecordDynamicSqlSupport.uuid, SqlBuilder.isEqualTo(sessionUUID))
                    .build()
                    .execute();

            if (execute != null && !execute.isEmpty()) {
                return this.toDomainModel(execute.get(0));
            } else {
                log.warn("Session with ID {} not found", sessionUUID);
                throw new NotFoundException("No Session with UUID: " + sessionUUID + " found");
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Long> pkByUUID(final String sessionUUID) {
        return Result.tryCatch(() -> {
            final List<Long> execute = this.sessionRecordMapper.selectIdsByExample()
                    .where(SessionRecordDynamicSqlSupport.uuid, SqlBuilder.isEqualTo(sessionUUID))
                    .build()
                    .execute();

            if (execute != null && !execute.isEmpty()) {
                return execute.get(0);
            } else {
                log.warn("Session with ID {} not found", sessionUUID);
                throw new NotFoundException("No Session with UUID: " + sessionUUID + " found");
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<Session>> allMatching(
            final FilterMap filterMap,
            final Predicate<Session> predicate) {

        return Result.tryCatch(() -> {

            final Boolean active = filterMap.getBooleanObject(API.ACTIVE_FILTER);
            return this.sessionRecordMapper
                    .selectByExample()
                    .where(
                            SessionRecordDynamicSqlSupport.terminationTime,
                            (active != null) ? active ? SqlBuilder.isNull() : SqlBuilder.isNotNull()
                                    : SqlBuilder.isEqualToWhenPresent(() -> null))
                    .and(
                            SessionRecordDynamicSqlSupport.groupId,
                            SqlBuilder.isEqualToWhenPresent(filterMap.getLong(Domain.SESSION.ATTR_GROUP_ID)))
                    .and(
                            SessionRecordDynamicSqlSupport.clientName,
                            isLikeWhenPresent(filterMap.getSQLWildcard(Domain.SESSION.ATTR_CLIENT_NAME)))
                    .and(
                            SessionRecordDynamicSqlSupport.clientMachineName,
                            isLikeWhenPresent(filterMap.getSQLWildcard(Domain.SESSION.ATTR_CLIENT_MACHINE_NAME)))
                    .and(
                            SessionRecordDynamicSqlSupport.clientVersion,
                            isLikeWhenPresent(filterMap.getSQLWildcard(Domain.SESSION.ATTR_CLIENT_VERSION)))
                    .and(
                            SessionRecordDynamicSqlSupport.clientIp,
                            isLikeWhenPresent(filterMap.getSQLWildcard(Domain.SESSION.ATTR_CLIENT_IP)))
                    .and(
                            SessionRecordDynamicSqlSupport.creationTime,
                            SqlBuilder.isGreaterThanOrEqualToWhenPresent(
                                    filterMap.getLong(Domain.CLIENT_ACCESS.ATTR_CREATION_TIME)))

                    .build()
                    .execute()
                    .stream()
                    .map(this::toDomainModel)
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional
    public Result<Session> createNew(final Session data) {
        return Result.tryCatch(() -> {

            final long now = Utils.getMillisecondsNow();
            final SessionRecord record = new SessionRecord(
                    null,
                    data.groupId,
                    (StringUtils.isNotBlank(data.uuid)) ? data.uuid : UUID.randomUUID().toString(),
                    (data.imageFormat != null) ? data.imageFormat.code : ImageFormat.PNG.code,
                    data.clientName,
                    data.clientIP,
                    data.clientMachineName,
                    data.clientOSName,
                    data.clientVersion,
                    now, now, null);

            this.sessionRecordMapper.insert(record);
            return record.getId();
        })
                .flatMap(this::byPK)
                .onError(TransactionHandler::rollback);
    }

    @Override
    public Result<Session> createNew(
            final Long groupId,
            final String uuid,
            final String userSessionName,
            final String clientIP,
            final String clientMachineName,
            final String clientOSName,
            final String clientVersion,
            final ImageFormat imageFormat) {

        return Result.tryCatch(() -> {

            final long now = Utils.getMillisecondsNow();
            final SessionRecord record = new SessionRecord(
                    null,
                    groupId,
                    (StringUtils.isNotBlank(uuid)) ? uuid : UUID.randomUUID().toString(),
                    (imageFormat != null) ? imageFormat.code : ImageFormat.PNG.code,
                    userSessionName,
                    clientIP,
                    clientMachineName,
                    clientOSName,
                    clientVersion,
                    now, now, null);

            this.sessionRecordMapper.insert(record);
            return record.getId();
        })
                .flatMap(this::byPK)
                .onError(TransactionHandler::rollback);
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
            final List<SessionRecord> sessions = this.sessionRecordMapper
                    .selectByExample()
                    .where(SessionRecordDynamicSqlSupport.id, isIn(ids))
                    .build()
                    .execute();

            // then delete the client access
            this.sessionRecordMapper
                    .deleteByExample()
                    .where(SessionRecordDynamicSqlSupport.id, isIn(ids))
                    .build()
                    .execute();

            return sessions.stream()
                    .map(rec -> new EntityKey(rec.getId(), EntityType.SESSION))
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional(readOnly = false)
    public Result<Session> save(final Session data) {
        return Result.tryCatch(() -> {

            final long now = Utils.getMillisecondsNow();
            final SessionRecord record = new SessionRecord(
                    data.id,
                    null,
                    null,
                    (data.imageFormat != null) ? null : data.imageFormat.code,
                    data.clientName,
                    data.clientIP,
                    data.clientMachineName,
                    data.clientOSName,
                    data.clientVersion,
                    null, now, null);

            this.sessionRecordMapper.updateByPrimaryKeySelective(record);
            return this.sessionRecordMapper.selectByPrimaryKey(data.id);
        })
                .map(this::toDomainModel)
                .onError(TransactionHandler::rollback);
    }

    @Override
    @Transactional
    public Result<String> closeSession(final String sessionUUID) {
        return Result.tryCatch(() -> {

            final long now = Utils.getMillisecondsNow();
            final Long id = this.sessionRecordMapper
                    .selectByExample()
                    .where(SessionRecordDynamicSqlSupport.uuid, SqlBuilder.isEqualTo(sessionUUID))
                    .build()
                    .execute()
                    .get(0)
                    .getId();

            final SessionRecord record = new SessionRecord(
                    id,
                    null, null, null, null, null, null, null, null,
                    null, now, now);

            this.sessionRecordMapper.updateByPrimaryKeySelective(record);
            return sessionUUID;
        })
                .onError(TransactionHandler::rollback);
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
                record.getTerminationTime(),
                record.getLastUpdateTime());
    }

}
