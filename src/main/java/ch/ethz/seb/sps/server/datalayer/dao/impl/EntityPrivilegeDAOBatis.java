/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.select.SelectDSL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.ethz.seb.sps.domain.api.API.PrivilegeType;
import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.user.EntityPrivilege;
import ch.ethz.seb.sps.server.datalayer.batis.custommappers.EntityPrivilegeIdMapper;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.EntityPrivilegeRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.EntityPrivilegeRecordMapper;
import ch.ethz.seb.sps.server.datalayer.batis.model.EntityPrivilegeRecord;
import ch.ethz.seb.sps.server.datalayer.dao.EntityPrivilegeDAO;
import ch.ethz.seb.sps.server.datalayer.dao.NoResourceFoundException;
import ch.ethz.seb.sps.utils.Result;

@Service
public class EntityPrivilegeDAOBatis implements EntityPrivilegeDAO {

    private final EntityPrivilegeRecordMapper entityPrivilegeRecordMapper;
    private final EntityPrivilegeIdMapper entityPrivilegeIdMapper;

    public EntityPrivilegeDAOBatis(
            final EntityPrivilegeRecordMapper entityPrivilegeRecordMapper,
            final EntityPrivilegeIdMapper entityPrivilegeIdMapper) {

        this.entityPrivilegeRecordMapper = entityPrivilegeRecordMapper;
        this.entityPrivilegeIdMapper = entityPrivilegeIdMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<EntityPrivilege>> getEntityPrivileges(final EntityType type, final Long entityId) {
        return Result.tryCatch(() -> {

            return this.entityPrivilegeRecordMapper.selectByExample()
                    .where(EntityPrivilegeRecordDynamicSqlSupport.entityType, SqlBuilder.isEqualTo(type.name()))
                    .and(EntityPrivilegeRecordDynamicSqlSupport.entityId, SqlBuilder.isEqualTo(entityId))
                    .build()
                    .execute()
                    .stream()
                    .map(this::toDomainObject)
                    .collect(Collectors.toList());

        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Set<Long>> getEntityIdsWithPrivilegeForUser(
            final EntityType type,
            final String userUUID,
            final PrivilegeType privilegeType) {

        return Result.tryCatch(() -> {

            final List<Long> result = SelectDSL.selectWithMapper(
                    this.entityPrivilegeIdMapper::selectIds,
                    EntityPrivilegeRecordDynamicSqlSupport.entityId)
                    .from(EntityPrivilegeRecordDynamicSqlSupport.entityPrivilegeRecord)
                    .where(EntityPrivilegeRecordDynamicSqlSupport.entityType, SqlBuilder.isEqualTo(type.name()))
                    .and(EntityPrivilegeRecordDynamicSqlSupport.userUuid, SqlBuilder.isEqualTo(userUUID))
                    .build()
                    .execute();

            return new HashSet<>(result);
        });
    }

    @Override
    public Result<Collection<EntityPrivilege>> getEntityPrivilegesForUser(final String userUUID) {
        return Result.tryCatch(() -> {
            return this.entityPrivilegeRecordMapper.selectByExample()
                    .where(EntityPrivilegeRecordDynamicSqlSupport.userUuid, SqlBuilder.isEqualTo(userUUID))
                    .build()
                    .execute()
                    .stream()
                    .map(this::toDomainObject)
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional
    public Result<EntityPrivilege> addPrivilege(
            final EntityType type,
            final Long entityId,
            final String userUUID,
            final PrivilegeType privilegeType) {

        return Result.tryCatch(() -> {

            // check duplication
            final List<Long> ids = this.entityPrivilegeRecordMapper.selectIdsByExample()
                    .where(EntityPrivilegeRecordDynamicSqlSupport.entityType, SqlBuilder.isEqualTo(type.name()))
                    .and(EntityPrivilegeRecordDynamicSqlSupport.entityId, SqlBuilder.isEqualTo(entityId))
                    .and(EntityPrivilegeRecordDynamicSqlSupport.userUuid, SqlBuilder.isEqualTo(userUUID))
                    .build()
                    .execute();

            // if we have duplication then just return thr existing one
            if (ids != null && !ids.isEmpty()) {
                return this.entityPrivilegeRecordMapper.selectByPrimaryKey(ids.get(0));
            }

            final EntityPrivilegeRecord entityPrivilegeRecord = new EntityPrivilegeRecord(
                    null,
                    type.name(),
                    entityId,
                    userUUID,
                    privilegeType.flag);

            this.entityPrivilegeRecordMapper.insert(entityPrivilegeRecord);

            return entityPrivilegeRecord;
        })
                .map(this::toDomainObject);
    }

    @Override
    @Transactional
    public Result<EntityKey> deletePrivilege(
            final EntityType type,
            final Long entityId,
            final String userUUID) {

        return Result.tryCatch(() -> {

            final List<Long> ids = this.entityPrivilegeRecordMapper.selectIdsByExample()
                    .where(EntityPrivilegeRecordDynamicSqlSupport.entityType, SqlBuilder.isEqualTo(type.name()))
                    .and(EntityPrivilegeRecordDynamicSqlSupport.entityId, SqlBuilder.isEqualTo(entityId))
                    .and(EntityPrivilegeRecordDynamicSqlSupport.userUuid, SqlBuilder.isEqualTo(userUUID))
                    .build()
                    .execute();

            if (ids == null || ids.isEmpty()) {
                throw new NoResourceFoundException(EntityType.ENTITY_PRIVILEGE,
                        "for type: " + type + " and id: " + entityId + " and userId: " + userUUID);
            }

            this.entityPrivilegeRecordMapper.deleteByExample()
                    .where(EntityPrivilegeRecordDynamicSqlSupport.entityType, SqlBuilder.isEqualTo(type.name()))
                    .and(EntityPrivilegeRecordDynamicSqlSupport.entityId, SqlBuilder.isEqualTo(entityId))
                    .and(EntityPrivilegeRecordDynamicSqlSupport.userUuid, SqlBuilder.isEqualTo(userUUID))
                    .build()
                    .execute();

            return new EntityKey(ids.get(0), EntityType.ENTITY_PRIVILEGE);

        });
    }

    @Override
    @Transactional
    public Result<Collection<EntityKey>> deleteAllPrivileges(
            final EntityType type,
            final Long entityId) {

        return Result.tryCatch(() -> {

            final List<Long> ids = this.entityPrivilegeRecordMapper.selectIdsByExample()
                    .where(EntityPrivilegeRecordDynamicSqlSupport.entityType, SqlBuilder.isEqualTo(type.name()))
                    .and(EntityPrivilegeRecordDynamicSqlSupport.entityId, SqlBuilder.isEqualTo(entityId))
                    .build()
                    .execute();

            if (ids == null || ids.isEmpty()) {
                return Collections.emptyList();
            }

            this.entityPrivilegeRecordMapper.deleteByExample()
                    .where(EntityPrivilegeRecordDynamicSqlSupport.id, SqlBuilder.isIn(ids))
                    .build()
                    .execute();

            return ids.stream()
                    .map(id -> new EntityKey(id, EntityType.ENTITY_PRIVILEGE))
                    .collect(Collectors.toList());
        });
    }

    private EntityPrivilege toDomainObject(final EntityPrivilegeRecord record) {
        return new EntityPrivilege(
                record.getId(),
                EntityType.valueOf(record.getEntityType()),
                record.getEntityId(),
                record.getUserUuid(),
                record.getPrivileges());
    }

}
