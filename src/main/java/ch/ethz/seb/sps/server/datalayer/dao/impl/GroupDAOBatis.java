/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao.impl;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.EntityPrivilege;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.screenshot.Group;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.GroupRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.GroupRecordMapper;
import ch.ethz.seb.sps.server.datalayer.batis.model.GroupRecord;
import ch.ethz.seb.sps.server.datalayer.dao.DuplicateEntityException;
import ch.ethz.seb.sps.server.datalayer.dao.EntityPrivilegeDAO;
import ch.ethz.seb.sps.server.datalayer.dao.GroupDAO;
import ch.ethz.seb.sps.server.servicelayer.UserService;
import ch.ethz.seb.sps.utils.Result;
import ch.ethz.seb.sps.utils.Utils;

@Service
public class GroupDAOBatis implements GroupDAO {

    private static final Logger log = LoggerFactory.getLogger(GroupDAOBatis.class);

    private final GroupRecordMapper groupRecordMapper;
    private final EntityPrivilegeDAO entityPrivilegeDAO;
    private final UserService userService;

    public GroupDAOBatis(
            final GroupRecordMapper groupRecordMapper,
            final EntityPrivilegeDAO entityPrivilegeDAO,
            final UserService userService) {

        this.groupRecordMapper = groupRecordMapper;
        this.entityPrivilegeDAO = entityPrivilegeDAO;
        this.userService = userService;
    }

    @Override
    public EntityType entityType() {
        return EntityType.SEB_GROUP;
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Group> byPK(final Long id) {
        return Result.tryCatch(() -> this.groupRecordMapper
                .selectByPrimaryKey(id))
                .map(this::toDomainModel);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUUID(final String groupUUID) {
        try {

            final Long count = this.groupRecordMapper
                    .countByExample()
                    .where(GroupRecordDynamicSqlSupport.uuid, SqlBuilder.isEqualTo(groupUUID))
                    .build()
                    .execute();

            return count != null && count.intValue() > 0;
        } catch (final Exception e) {
            log.error("Failed to check group exists: ", e);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Long> getGroupIdByUUID(final String groupUUID) {
        return Result.tryCatch(() -> this.groupRecordMapper
                .selectIdsByExample()
                .where(GroupRecordDynamicSqlSupport.uuid, SqlBuilder.isEqualTo(groupUUID))
                .build()
                .execute()
                .get(0));
    }

    @Override
    public Result<Collection<Group>> allOf(final Set<Long> pks) {
        return Result.tryCatch(() -> {

            if (pks == null || pks.isEmpty()) {
                return Collections.emptyList();
            }

            return this.groupRecordMapper
                    .selectByExample()
                    .where(GroupRecordDynamicSqlSupport.id, isIn(new ArrayList<>(pks)))
                    .build()
                    .execute()
                    .stream()
                    .map(this::toDomainModel)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public Result<Collection<Group>> allMatching(final FilterMap filterMap, final Predicate<Group> predicate) {
        return Result.tryCatch(() -> {

            final Boolean active = filterMap.getBooleanObject(API.ACTIVE_FILTER);

            return this.groupRecordMapper
                    .selectByExample()
                    .where(
                            GroupRecordDynamicSqlSupport.terminationTime,
                            (active != null) ? active ? SqlBuilder.isNull() : SqlBuilder.isNotNull()
                                    : SqlBuilder.isEqualToWhenPresent(null))
                    .and(
                            GroupRecordDynamicSqlSupport.name,
                            isLikeWhenPresent(filterMap.getSQLWildcard(Domain.SEB_GROUP.ATTR_NAME)))
                    .and(
                            GroupRecordDynamicSqlSupport.description,
                            isLikeWhenPresent(filterMap.getSQLWildcard(Domain.SEB_GROUP.ATTR_DESCRIPTION)))
                    .and(
                            GroupRecordDynamicSqlSupport.creationTime,
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
    @Transactional(readOnly = true)
    public boolean isActive(final String modelId) {
        if (StringUtils.isBlank(modelId)) {
            return false;
        }

        return this.groupRecordMapper
                .countByExample()
                .where(GroupRecordDynamicSqlSupport.id, isEqualTo(Long.valueOf(modelId)))
                .and(GroupRecordDynamicSqlSupport.terminationTime, SqlBuilder.isNull())
                .build()
                .execute() > 0;
    }

    @Override
    @Transactional
    public Result<Collection<EntityKey>> setActive(final Set<EntityKey> all, final boolean active) {
        return Result.tryCatch(() -> {

            final List<Long> ids = extractListOfPKs(all);
            if (ids == null || ids.isEmpty()) {
                return Collections.emptyList();
            }

            final long now = Utils.getMillisecondsNow();

            final GroupRecord newRecord = new GroupRecord(
                    null, null, null, null, null, null,
                    now,
                    active ? null : now);

            this.groupRecordMapper.updateByExampleSelective(newRecord)
                    .where(GroupRecordDynamicSqlSupport.id, isIn(ids))
                    .build()
                    .execute();

            return this.groupRecordMapper.selectByExample()
                    .where(GroupRecordDynamicSqlSupport.id, isIn(ids))
                    .build()
                    .execute()
                    .stream()
                    .map(record -> new EntityKey(record.getId(), EntityType.SEB_GROUP))
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional
    public Result<Group> createNew(final String groupUUID) {
        return createNew(new Group(null, groupUUID, groupUUID, groupUUID, null, null, null, null, null));
    }

    @Override
    @Transactional
    public Result<Group> createNew(final Group data) {
        return Result.tryCatch(() -> {

            checkUniqueName(data);

            final long millisecondsNow = Utils.getMillisecondsNow();
            final GroupRecord newRecord = new GroupRecord(
                    null,
                    (StringUtils.isNotBlank(data.uuid)) ? data.uuid : UUID.randomUUID().toString(),
                    data.name,
                    data.description,
                    this.userService.getCurrentUserUUIDOrNull(),
                    millisecondsNow,
                    millisecondsNow,
                    null);

            this.groupRecordMapper.insert(newRecord);
            return this.groupRecordMapper.selectByPrimaryKey(newRecord.getId());
        })
                .map(this::toDomainModel)
                .onError(TransactionHandler::rollback);
    }

    @Override
    @Transactional
    public Result<Group> save(final Group data) {
        return Result.tryCatch(() -> {

            final long millisecondsNow = Utils.getMillisecondsNow();
            final GroupRecord newRecord = new GroupRecord(
                    data.id,
                    null,
                    data.name,
                    data.description,
                    null,
                    null,
                    millisecondsNow,
                    null);

            this.groupRecordMapper.updateByPrimaryKeySelective(newRecord);
            this.entityPrivilegeDAO.savePut(EntityType.SEB_GROUP, data.id, data.entityPrivileges);
            return this.groupRecordMapper.selectByPrimaryKey(data.id);
        })
                .map(this::toDomainModel)
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
            final List<GroupRecord> groups = this.groupRecordMapper
                    .selectByExample()
                    .where(GroupRecordDynamicSqlSupport.id, isIn(ids))
                    .build()
                    .execute();

            // then delete the client access
            this.groupRecordMapper
                    .deleteByExample()
                    .where(GroupRecordDynamicSqlSupport.id, isIn(ids))
                    .build()
                    .execute();

            return groups.stream()
                    .map(rec -> new EntityKey(rec.getId(), EntityType.SEB_GROUP))
                    .collect(Collectors.toList());
        });
    }

    private Group toDomainModel(final GroupRecord record) {
        return new Group(
                record.getId(),
                record.getUuid(),
                record.getName(),
                record.getDescription(),
                record.getOwner(),
                record.getCreationTime(),
                record.getLastUpdateTime(),
                record.getTerminationTime(),
                getEntityPrivileges(record.getId()));
    }

    private Collection<EntityPrivilege> getEntityPrivileges(final Long id) {
        try {

            if (id == null) {
                return Collections.emptyList();
            }

            return this.entityPrivilegeDAO
                    .getEntityPrivileges(EntityType.SEB_GROUP, id)
                    .getOrThrow();

        } catch (final Exception e) {
            log.error("Failed to get entity privileges for Group: {}", id, e);
            return Collections.emptyList();
        }
    }

    private void checkUniqueName(final Group group) {

        final Long otherWithSameName = this.groupRecordMapper
                .countByExample()
                .where(GroupRecordDynamicSqlSupport.name, isEqualTo(group.name))
                .and(GroupRecordDynamicSqlSupport.id, isNotEqualToWhenPresent(group.id))
                .build()
                .execute();

        if (otherWithSameName != null && otherWithSameName > 0) {
            throw new DuplicateEntityException(
                    EntityType.CLIENT_ACCESS,
                    Domain.CLIENT_ACCESS.ATTR_NAME,
                    "clientaccess:name:name.notunique");
        }
    }

}
