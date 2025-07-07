/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao.impl;

import static ch.ethz.seb.sps.server.datalayer.batis.mapper.GroupRecordDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.select.SelectDSL;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(EntityPrivilegeDAOBatis.class);

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
    public Result<Collection<EntityPrivilege>> getEntityPrivileges(final EntityType entityType, final Long entityId) {
        return Result.tryCatch(() -> {

            return this.entityPrivilegeRecordMapper.selectByExample()
                    .where(EntityPrivilegeRecordDynamicSqlSupport.entityType, SqlBuilder.isEqualTo(entityType.name()))
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
            final EntityType entityType,
            final String userUUID,
            final PrivilegeType privilegeType) {

        return Result.tryCatch(() -> {

            final List<Long> result = SelectDSL.selectWithMapper(
                    this.entityPrivilegeIdMapper::selectIds,
                    EntityPrivilegeRecordDynamicSqlSupport.entityId)
                    .from(EntityPrivilegeRecordDynamicSqlSupport.entityPrivilegeRecord)
                    .where(EntityPrivilegeRecordDynamicSqlSupport.entityType, SqlBuilder.isEqualTo(entityType.name()))
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
            final EntityType entityType,
            final Long entityId,
            final String userUUID,
            final PrivilegeType privilegeType) {

        return Result.tryCatch(() -> {

            // check duplication
            final List<Long> ids = this.entityPrivilegeRecordMapper.selectIdsByExample()
                    .where(EntityPrivilegeRecordDynamicSqlSupport.entityType, SqlBuilder.isEqualTo(entityType.name()))
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
                    entityType.name(),
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
            final EntityType entityType,
            final Long entityId,
            final String userUUID) {

        return Result.tryCatch(() -> {

            final List<Long> ids = this.entityPrivilegeRecordMapper.selectIdsByExample()
                    .where(EntityPrivilegeRecordDynamicSqlSupport.entityType, SqlBuilder.isEqualTo(entityType.name()))
                    .and(EntityPrivilegeRecordDynamicSqlSupport.entityId, SqlBuilder.isEqualTo(entityId))
                    .and(EntityPrivilegeRecordDynamicSqlSupport.userUuid, SqlBuilder.isEqualTo(userUUID))
                    .build()
                    .execute();

            if (ids == null || ids.isEmpty()) {
                throw new NoResourceFoundException(EntityType.ENTITY_PRIVILEGE,
                        "for entityType: " + entityType + " and id: " + entityId + " and userId: " + userUUID);
            }

            this.entityPrivilegeRecordMapper.deleteByExample()
                    .where(EntityPrivilegeRecordDynamicSqlSupport.entityType, SqlBuilder.isEqualTo(entityType.name()))
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
            final EntityType entityType,
            final Long entityId) {

        return Result.tryCatch(() -> {

            final List<Long> ids = this.entityPrivilegeRecordMapper.selectIdsByExample()
                    .where(EntityPrivilegeRecordDynamicSqlSupport.entityType, SqlBuilder.isEqualTo(entityType.name()))
                    .and(EntityPrivilegeRecordDynamicSqlSupport.entityId, SqlBuilder.isEqualTo(entityId))
                    .build()
                    .execute();

            if (ids == null || ids.isEmpty()) {
                return Collections.emptyList();
            }

            this.entityPrivilegeRecordMapper.deleteByExample()
                    .where(EntityPrivilegeRecordDynamicSqlSupport.id, isIn(ids))
                    .build()
                    .execute();

            return ids.stream()
                    .map(id -> new EntityKey(id, EntityType.ENTITY_PRIVILEGE))
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional
    public void updatePrivilege(Long epId, PrivilegeType privilegeType) {
       try {

           UpdateDSL.updateWithMapper(
                   this.entityPrivilegeRecordMapper::update,
                           EntityPrivilegeRecordDynamicSqlSupport.entityPrivilegeRecord)
                   .set(EntityPrivilegeRecordDynamicSqlSupport.privileges).equalTo(privilegeType.flag)
                   .where(id, isEqualTo(epId))
                   .build()
                   .execute();

       } catch (Exception e) {
           log.warn(
                   "Failed to update entity privilege for privilege: {} error: {}",
                   epId,
                   e.getMessage());
       }
    }

    @Override
    @Transactional
    public void updatePrivileges(Collection<Long> epIds, PrivilegeType privilegeType) {
        try {
            
            if (epIds == null || epIds.isEmpty()) {
                return;
            }

            UpdateDSL.updateWithMapper(
                            this.entityPrivilegeRecordMapper::update,
                            EntityPrivilegeRecordDynamicSqlSupport.entityPrivilegeRecord)
                    .set(EntityPrivilegeRecordDynamicSqlSupport.privileges).equalTo(privilegeType.flag)
                    .where(id, isIn(epIds))
                    .build()
                    .execute();

        } catch (Exception e) {
            log.warn(
                    "Failed to update entity privileges for: {} with: {} error: {}",
                    epIds,
                    privilegeType,
                    e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteAllPrivilegesForUser(String user_uuid) {
        try {

            this.entityPrivilegeRecordMapper.deleteByExample()
                    .where(EntityPrivilegeRecordDynamicSqlSupport.userUuid, SqlBuilder.isEqualTo(user_uuid))
                    .build()
                    .execute();
            
        } catch (Exception e) {
            log.warn(
                    "Failed to delete entity privilege for user: {} error: {}",
                    user_uuid,
                    e.getMessage());
        }
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
