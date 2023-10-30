/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.api.JSONMapper;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.AuditLogRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.AuditLogRecordMapper;
import ch.ethz.seb.sps.server.datalayer.batis.model.AuditLogRecord;
import ch.ethz.seb.sps.server.datalayer.dao.NoResourceFoundException;
import ch.ethz.seb.sps.utils.Constants;
import ch.ethz.seb.sps.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.user.AuditLog;
import ch.ethz.seb.sps.domain.model.user.AuditLog.AuditLogType;
import ch.ethz.seb.sps.domain.model.user.UserAccount;
import ch.ethz.seb.sps.domain.model.user.UserInfo;
import ch.ethz.seb.sps.server.datalayer.dao.AuditLogDAO;
import ch.ethz.seb.sps.utils.Result;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;
import static org.mybatis.dynamic.sql.SqlBuilder.isInCaseInsensitiveWhenPresent;

@Lazy
@Component
public class AuditLogDAOBatis implements AuditLogDAO {

    private final AuditLogRecordMapper auditLogRecordMapper;
    private final JSONMapper jsonMapper;

    public AuditLogDAOBatis(
            final AuditLogRecordMapper auditLogRecordMapper,
            final JSONMapper jsonMapper
    ){
        this.auditLogRecordMapper = auditLogRecordMapper;
        this.jsonMapper = jsonMapper;
    }


    @Override
    public <T extends Entity> Result<T> log(final UserInfo userInfo, final AuditLogType logType, final T entity) {
        return prepareLog(userInfo, logType, entity, toMessage(entity));
    }

    @Override
    public void logLogin(final UserInfo userInfo) {
        log(userInfo, AuditLogType.LOGIN, userInfo);
    }

    @Override
    public void logLogout(final UserInfo userInfo) {
        log(userInfo, AuditLogType.LOGOUT, userInfo);
    }

    @Override
    public Result<UserAccount> logRegisterAccount(final UserInfo userInfo) {
        return log(userInfo, AuditLogType.REGISTER, userInfo);
    }

    @Override
    public <T extends Entity> Result<T> logCreate(final UserInfo userInfo, final T entity) {
        return log(userInfo, AuditLogType.CREATE, entity);
    }

    @Override
    public <T extends Entity> Result<T> logModify(final UserInfo userInfo, final T entity) {
        return log(userInfo, AuditLogType.MODIFY, entity);
    }

    @Override
    public <T extends Entity> Result<T> logDelete(final UserInfo userInfo, final T entity) {
        return log(userInfo, AuditLogType.DELETE, entity);
    }

    @Override
    public Result<Collection<EntityKey>> logDeleted(final UserInfo userInfo, final Collection<EntityKey> entities) {
        return Result.of(entities);
    }


    @Override
    public EntityType entityType() {
        return EntityType.AUDIT_LOG;
    }

    @Override
    public Result<AuditLog> byPK(final Long id) {
        return Result.tryCatch(() -> {
            final AuditLogRecord auditLogRecord = this.auditLogRecordMapper.selectByPrimaryKey(id);

            if(auditLogRecord == null){
                throw new NoResourceFoundException(EntityType.AUDIT_LOG, String.valueOf(id));
            }

           return toDomainModel(auditLogRecord);
        });
    }

    @Override
    public Result<Collection<AuditLog>> allOf(final Set<Long> pks) {
        return Result.tryCatch(() -> {
            final List<AuditLog> result = pks
                    .stream()
                    .map(pk -> this.auditLogRecordMapper.selectByPrimaryKey(pk))
                    .map(this::toDomainModel)
                    .collect(Collectors.toList());

            return result;
        });
    }

    @Override
    public Result<AuditLog> createNew(final AuditLog data) {

        final String message = toMessage(data);;

        return Result.tryCatch(() -> {
            writeLogIntoDB(
                    data.userUUID,
                    AuditLogType.CREATE,
                    data.entityType,
                    data.id,
                    message
            );

            return data;
        });
    }

    @Override
    public Result<AuditLog> save(final AuditLog data) {
        throw new UnsupportedOperationException();
    }


    @Override
    public Result<Collection<EntityKey>> delete(final Set<EntityKey> all) {
        return Result.tryCatch(() -> {

            final List<Long> ids = extractListOfPKs(all);
            if (ids == null || ids.isEmpty()) {
                return Collections.emptyList();
            }

            this.auditLogRecordMapper.deleteByExample()
                    .where(AuditLogRecordDynamicSqlSupport.id, isIn(ids))
                    .build()
                    .execute();

            return ids.stream()
                    .map(id -> new EntityKey(id, EntityType.AUDIT_LOG))
                    .collect(Collectors.toList());
        });
    }

    @Override
    public Result<Collection<AuditLog>> allMatching(
            final FilterMap filterMap,
            final Collection<Long> prePredicated) {

        return Result.tryCatch(() -> {

            final Long fromTime = filterMap.getLong(API.PARAM_FROM_TIME);
            final Long toTime = filterMap.getLong(API.PARAM_TO_TIME);

            final List<AuditLog> result = this.auditLogRecordMapper
                    .selectByExample()
                    .where(
                            AuditLogRecordDynamicSqlSupport.timestamp,
                            SqlBuilder.isGreaterThanOrEqualToWhenPresent(fromTime))
                    .and(
                            AuditLogRecordDynamicSqlSupport.timestamp,
                            SqlBuilder.isLessThanWhenPresent(toTime))
                    .and(
                            AuditLogRecordDynamicSqlSupport.userUuid,
                            isEqualToWhenPresent(filterMap.getSQLWildcard(Domain.AUDIT_LOG.ATTR_USER_UUID)))
                    .build()
                    .execute()
                    .stream()
                    .map(this::toDomainModel)
                    .collect(Collectors.toList());

            return result;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Set<Long>> getAllOwnedIds(final String userUUID) {
        return Result.of(Collections.emptySet());
    }

    private <E extends Entity> Result<E> prepareLog(
            final UserInfo userInfo,
            final AuditLogType logType,
            final E entity,
            final String message){

        return Result.tryCatch(() -> {
                    String _message = message;
                    if (message == null) {
                        _message = "Entity details: " + entity;
                    }

                    //writes log into DB
                    writeLogIntoDB(userInfo.uuid, logType, entity.entityType(), entity.getId(), _message);

                    return entity;
                })
                .onError(TransactionHandler::rollback)
                .onError(transaction -> log.error(
                        "Unexpected error while trying to log user activity for user {}, action-type: {} entity-type: {} entity-id: {}",
                        userInfo.uuid,
                        logType,
                        entity.entityType().name(),
                        entity.getModelId(),
                        transaction));
    }


    private void writeLogIntoDB(
            final String userUUID,
            final AuditLogType logType,
            final EntityType entityType,
            final Long entityId,
            final String message) {

        this.auditLogRecordMapper.insertSelective(
                new AuditLogRecord(
                        null,
                        userUUID,
                        System.currentTimeMillis(),
                        logType.name(),
                        entityType.name(),
                        entityId,
                        Utils.truncateText(message, 4000)
                )
        );
    }

    private String toMessage(final Entity entity) {
        if (entity == null) {
            return Constants.EMPTY_NOTE;
        }

        String entityAsString;
        try {
            entityAsString = entity.getName() + " = " + this.jsonMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(entity.printSecureCopy());
        } catch (final JsonProcessingException e) {
            entityAsString = entity.toString();
        }

        if (entityAsString != null && entityAsString.length() > 4000) {
            return Utils.truncateText(entityAsString, 4000);
        }
        return entityAsString;
    }

    private AuditLog toDomainModel(final AuditLogRecord auditLogRecord){
        return new AuditLog(
                auditLogRecord.getId(),
                auditLogRecord.getUserUuid(),
                "",
                auditLogRecord.getTimestamp(),
                AuditLogType.valueOf(auditLogRecord.getActivityType()),
                EntityType.valueOf(auditLogRecord.getEntityType()),
                auditLogRecord.getEntityId().toString(),
                auditLogRecord.getMessage()
        );
    }

}