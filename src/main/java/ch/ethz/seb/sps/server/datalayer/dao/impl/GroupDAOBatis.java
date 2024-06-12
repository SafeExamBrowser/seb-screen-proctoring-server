/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao.impl;

import static ch.ethz.seb.sps.server.datalayer.batis.mapper.GroupRecordDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.service.ExamViewData;
import ch.ethz.seb.sps.domain.model.service.Group;
import ch.ethz.seb.sps.domain.model.service.GroupViewData;
import ch.ethz.seb.sps.domain.model.user.EntityPrivilege;
import ch.ethz.seb.sps.server.datalayer.batis.custommappers.GroupViewMapper;
import ch.ethz.seb.sps.server.datalayer.batis.customrecords.GroupViewRecord;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ExamRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.GroupRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.GroupRecordMapper;
import ch.ethz.seb.sps.server.datalayer.batis.model.GroupRecord;
import ch.ethz.seb.sps.server.datalayer.dao.DuplicateEntityException;
import ch.ethz.seb.sps.server.datalayer.dao.EntityPrivilegeDAO;
import ch.ethz.seb.sps.server.datalayer.dao.GroupDAO;
import ch.ethz.seb.sps.server.datalayer.dao.NoResourceFoundException;
import ch.ethz.seb.sps.server.datalayer.dao.OwnedEntityDAO;
import ch.ethz.seb.sps.server.datalayer.dao.SessionDAO;
import ch.ethz.seb.sps.server.weblayer.BadRequestException;
import ch.ethz.seb.sps.utils.Result;
import ch.ethz.seb.sps.utils.Utils;

@Service
public class GroupDAOBatis implements GroupDAO, OwnedEntityDAO {

    private static final Logger log = LoggerFactory.getLogger(GroupDAOBatis.class);

    private final GroupRecordMapper groupRecordMapper;
    private final GroupViewMapper groupViewMapper;
    private final EntityPrivilegeDAO entityPrivilegeDAO;
    private final SessionDAO sessionDAO;

    public GroupDAOBatis(
            final GroupRecordMapper groupRecordMapper,
            final EntityPrivilegeDAO entityPrivilegeDAO,
            final SessionDAO sessionDAO,
            final GroupViewMapper groupViewMapper) {

        this.groupRecordMapper = groupRecordMapper;
        this.groupViewMapper = groupViewMapper;
        this.entityPrivilegeDAO = entityPrivilegeDAO;
        this.sessionDAO = sessionDAO;
    }

    @Override
    public EntityType entityType() {
        return EntityType.SEB_GROUP;
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
    public Result<Group> byPK(final Long id) {
        return recordByPK(id)
                .map(this::toDomainModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Group> byModelId(final String id) {
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
    public Result<Set<Long>> getAllOwnedEntityPKs(final String userUUID) {
        return Result.tryCatch(() -> {
            return Utils.immutableSetOf(this.groupRecordMapper
                    .selectIdsByExample()
                    .where(GroupRecordDynamicSqlSupport.owner, isEqualTo(userUUID))
                    .build()
                    .execute());
        });
    }

    @Override
    @Transactional(readOnly = true)
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
    public Result<GroupViewData> getGroupWithExamData(final Long groupId) {
        return Result.tryCatch(() -> {
            final GroupViewRecord groupViewRecord = this.groupViewMapper
                    .getGroupWithExamData()

                    .where(
                            GroupRecordDynamicSqlSupport.id,
                            SqlBuilder.isEqualTo(groupId)

                    )

                    .build()
                    .execute();

            return toGroupWithExamDomainModel(groupViewRecord);
        });
    }

    @Override
    public Result<Collection<Long>> getGroupIdsWithExamData(
            final FilterMap filterMap,
            final Collection<Long> prePredicated) {

        return Result.tryCatch(() -> {

            final Boolean active = filterMap.getBooleanObject(API.ACTIVE_FILTER);
            final Long fromTime = filterMap.getLong(API.PARAM_FROM_TIME);
            final Long toTime = filterMap.getLong(API.PARAM_TO_TIME);

            final List<Long> result = this.groupViewMapper
                    .getGroupIdsWithExamData()

                    .where(
                            GroupRecordDynamicSqlSupport.terminationTime,
                            (active != null) ? active ? SqlBuilder.isNull() : SqlBuilder.isNotNull()
                                    : SqlBuilder.isEqualToWhenPresent(() -> null))
                    .and(
                            GroupRecordDynamicSqlSupport.name,
                            isLikeWhenPresent(filterMap.getSQLWildcard(Domain.SEB_GROUP.ATTR_NAME)))
                    .and(
                            GroupRecordDynamicSqlSupport.description,
                            isLikeWhenPresent(filterMap.getSQLWildcard(Domain.SEB_GROUP.ATTR_DESCRIPTION)))
                    .and(
                            ExamRecordDynamicSqlSupport.name,
                            isLikeWhenPresent(filterMap.getSQLWildcard(API.PARAM_EXAM_NAME)))
                    .and(
                            GroupRecordDynamicSqlSupport.name,
                            isLikeWhenPresent(filterMap.getSQLWildcard(API.PARAM_GROUP_NAME)))
                    .and(
                            GroupRecordDynamicSqlSupport.id,
                            SqlBuilder.isInWhenPresent((prePredicated == null)
                                    ? Collections.emptyList()
                                    : prePredicated))

                    .build()
                    .execute()
                    .stream()
                    .collect(Collectors.toList());

            return result;
        });

    }

    @Override
    public Result<Collection<GroupViewData>> getGroupsWithExamData(
            final FilterMap filterMap,
            final Collection<Long> prePredicated) {

        return Result.tryCatch(() -> {

            final Boolean active = filterMap.getBooleanObject(API.ACTIVE_FILTER);
            final Long fromTime = filterMap.getLong(API.PARAM_FROM_TIME);
            final Long toTime = filterMap.getLong(API.PARAM_TO_TIME);

            final List<GroupViewData> result = this.groupViewMapper
                    .getGroupsWithExamData()

                    .where(
                            GroupRecordDynamicSqlSupport.terminationTime,
                            (active != null) ? active ? SqlBuilder.isNull() : SqlBuilder.isNotNull()
                                    : SqlBuilder.isEqualToWhenPresent(() -> null))
                    .and(
                            GroupRecordDynamicSqlSupport.name,
                            isLikeWhenPresent(filterMap.getSQLWildcard(Domain.SEB_GROUP.ATTR_NAME)))
                    .and(
                            GroupRecordDynamicSqlSupport.description,
                            isLikeWhenPresent(filterMap.getSQLWildcard(Domain.SEB_GROUP.ATTR_DESCRIPTION)))
                    .and(
                            ExamRecordDynamicSqlSupport.name,
                            isLikeWhenPresent(filterMap.getSQLWildcard(API.PARAM_EXAM_NAME)))
                    .and(
                            GroupRecordDynamicSqlSupport.creationTime,
                            SqlBuilder.isGreaterThanOrEqualToWhenPresent(fromTime))
                    .and(
                            GroupRecordDynamicSqlSupport.creationTime,
                            SqlBuilder.isLessThanOrEqualToWhenPresent(toTime))
                    .and(
                            GroupRecordDynamicSqlSupport.id,
                            SqlBuilder.isInWhenPresent((prePredicated == null)
                                    ? Collections.emptyList()
                                    : prePredicated))

                    .build()
                    .execute()
                    .stream()
                    .map(this::toGroupWithExamDomainModel)
                    .collect(Collectors.toList());

            return result;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<Group>> allMatching(
            final FilterMap filterMap,
            final Collection<Long> prePredicated) {

        return Result.tryCatch(() -> {

            final Boolean active = filterMap.getBooleanObject(API.ACTIVE_FILTER);
            final Long fromTime = filterMap.getLong(API.PARAM_FROM_TIME);
            final Long toTime = filterMap.getLong(API.PARAM_TO_TIME);

            final List<Group> result = this.groupRecordMapper
                    .selectByExample()
                    .where(
                            GroupRecordDynamicSqlSupport.terminationTime,
                            (active != null) ? active ? SqlBuilder.isNull() : SqlBuilder.isNotNull()
                                    : SqlBuilder.isEqualToWhenPresent(() -> null))
                    .and(
                            GroupRecordDynamicSqlSupport.name,
                            isLikeWhenPresent(filterMap.getSQLWildcard(Domain.SEB_GROUP.ATTR_NAME)))
                    .and(
                            GroupRecordDynamicSqlSupport.examId,
                            isEqualToWhenPresent(filterMap.getLong(Domain.SEB_GROUP.ATTR_EXAM_ID)))
                    .and(
                            GroupRecordDynamicSqlSupport.description,
                            isLikeWhenPresent(filterMap.getSQLWildcard(Domain.SEB_GROUP.ATTR_DESCRIPTION)))
                    .and(
                            GroupRecordDynamicSqlSupport.creationTime,
                            SqlBuilder.isGreaterThanOrEqualToWhenPresent(fromTime))
                    .and(
                            GroupRecordDynamicSqlSupport.creationTime,
                            SqlBuilder.isLessThanOrEqualToWhenPresent(toTime))
                    .and(
                            GroupRecordDynamicSqlSupport.id,
                            SqlBuilder.isInWhenPresent((prePredicated == null)
                                    ? Collections.emptyList()
                                    : prePredicated))
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
        return Result.tryCatch(() -> {
            final List<Long> result = this.groupRecordMapper
                    .selectIdsByExample()
                    .where(
                            GroupRecordDynamicSqlSupport.owner,
                            SqlBuilder.isEqualTo(userUUID))
                    .build()
                    .execute();

            return Utils.immutableSetOf(result);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<Group>> byGroupName(final FilterMap filterMap) {
        return Result.tryCatch(() -> {
            return this.groupRecordMapper
                    .selectByExample()
                    .where(
                            GroupRecordDynamicSqlSupport.terminationTime, SqlBuilder.isNull())
                    .and(
                            GroupRecordDynamicSqlSupport.name,
                            isLikeWhenPresent(filterMap.getSQLWildcard(API.PARAM_GROUP_NAME)))
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
    public Result<EntityKey> setActive(final EntityKey entityKey, final boolean active) {
        return pkByUUID(entityKey.modelId)
                .map(pk -> {

                    final long now = Utils.getMillisecondsNow();

                    UpdateDSL.updateWithMapper(this.groupRecordMapper::update, groupRecord)
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
    public Result<Collection<EntityKey>> applyActivationForAllOfExam(final Long examId, final boolean activation) {
        return Result.tryCatch(() -> {

            final long now = Utils.getMillisecondsNow();

            final List<Long> groupIds = this.groupRecordMapper.selectIdsByExample()
                    .where(GroupRecordDynamicSqlSupport.examId, isEqualTo(examId))
                    .build()
                    .execute();

            UpdateDSL.updateWithMapper(this.groupRecordMapper::update, groupRecord)
                    .set(lastUpdateTime).equalTo(now)
                    .set(terminationTime).equalTo(() -> activation ? null : now)
                    .where(id, isIn(groupIds))
                    .build()
                    .execute();

            return groupIds.stream()
                    .map(id -> new EntityKey(id, EntityType.SEB_GROUP))
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional
    public Result<Group> createNew(final String groupUUID) {
        return createNew(new Group(null, groupUUID, groupUUID, groupUUID, null, null, null, null, null, null));
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
                    data.owner,
                    millisecondsNow,
                    millisecondsNow,
                    null,
                    data.exam_id);

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

            Long pk = data.id;
            if (pk == null && data.uuid != null) {
                pk = this.pkByUUID(data.uuid).getOr(null);
            }
            if (pk == null) {
                throw new BadRequestException("group save", "no group with uuid: " + data.uuid + "found");
            }

            checkUniqueName(data);

            UpdateDSL.updateWithMapper(this.groupRecordMapper::update, groupRecord)
                    .set(name).equalTo(data.name)
                    .set(description).equalTo(data.description)
                    .set(lastUpdateTime).equalTo(millisecondsNow)
                    .where(id, isEqualTo(pk))
                    .build()
                    .execute();

            return this.groupRecordMapper.selectByPrimaryKey(pk);
        })
                .map(this::toDomainModel)
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
    public Result<Collection<EntityKey>> deleteAllForExams(final List<Long> examPKs) {
        return Result.tryCatch(() -> this.groupRecordMapper
                .selectIdsByExample()
                .where(GroupRecordDynamicSqlSupport.examId, isIn(examPKs))
                .build()
                .execute())
                .map(this::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<Long>> allIdsForExamsIds(final Collection<Long> examPKs) {
        return Result.tryCatch(() -> this.groupRecordMapper
                    .selectIdsByExample()
                    .where(GroupRecordDynamicSqlSupport.examId, isIn(examPKs))
                    .build()
                    .execute());
    }

    @Override
    public Boolean isExamRunning(final Long examEndTime){
        if(examEndTime == null){
            return null;
        }

        if(examEndTime >= Utils.getMillisecondsNow()){
            return true;
        }

        return false;
    }

    private Result<GroupRecord> recordByPK(final Long pk) {
        return Result.tryCatch(() -> {

            final GroupRecord selectByPrimaryKey = this.groupRecordMapper.selectByPrimaryKey(pk);

            if (selectByPrimaryKey == null) {
                throw new NoResourceFoundException(EntityType.SEB_GROUP, String.valueOf(pk));
            }

            return selectByPrimaryKey;
        });
    }

    private Collection<EntityKey> delete(final List<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        // delete all session data for all involved groups first
        final Collection<EntityKey> deletedSessions = this.sessionDAO
                .deleteAllForGroups(ids)
                .getOrThrow();

        log.info("Deleted following sessions: {} before deleting groups: {}", deletedSessions, ids);

        // delete all involved entity privileges
        deleteAllEntityPrivileges(ids, this.entityPrivilegeDAO);

        // then the groups
        this.groupRecordMapper
                .deleteByExample()
                .where(GroupRecordDynamicSqlSupport.id, isIn(ids))
                .build()
                .execute();

        return ids.stream()
                .map(pk -> new EntityKey(pk, EntityType.SEB_GROUP))
                .collect(Collectors.toList());
    }

    private Result<GroupRecord> recordByUUID(final String uuid) {
        return Result.tryCatch(() -> {

            final List<GroupRecord> execute = this.groupRecordMapper.selectByExample()
                    .where(GroupRecordDynamicSqlSupport.uuid, isEqualTo(uuid))
                    .build()
                    .execute();

            if (execute == null || execute.isEmpty()) {
                throw new NoResourceFoundException(EntityType.SEB_GROUP, uuid);
            }

            return execute.get(0);
        });
    }

    private Result<Long> pkByUUID(final String groupUUID) {

        return Result.tryCatch(() -> {
            final List<Long> execute = this.groupRecordMapper
                    .selectIdsByExample()
                    .where(GroupRecordDynamicSqlSupport.uuid, SqlBuilder.isEqualTo(groupUUID))
                    .build()
                    .execute();

            if (execute == null || execute.isEmpty()) {
                throw new NoResourceFoundException(EntityType.SEB_GROUP, groupUUID);
            }

            return execute.get(0);
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
                record.getExamId(),
                getEntityPrivileges(record.getId(), record.getExamId()));
    }

    private GroupViewData toGroupWithExamDomainModel(final GroupViewRecord record) {
        return new GroupViewData(
                record.getId(),
                record.getUuid(),
                record.getName(),
                record.getDescription(),
                record.getOwner(),
                record.getCreationTime(),
                record.getLastUpdateTime(),
                record.getTerminationTime(),
                new ExamViewData(record.getExamUuid(), record.getExamName(), isExamRunning(record.getExamEndTime()), record.getExamStartTime(), record.getExamEndTime()));
    }

    private Collection<EntityPrivilege> getEntityPrivileges(final Long id, final Long examId) {
        try {

            if (id == null) {
                return Collections.emptyList();
            }

            final Collection<EntityPrivilege> groupPrivileges = this.entityPrivilegeDAO
                    .getEntityPrivileges(EntityType.SEB_GROUP, id)
                    .getOrThrow();

            if (examId != null) {
                final Collection<EntityPrivilege> examPrivileges = this.entityPrivilegeDAO
                        .getEntityPrivileges(EntityType.EXAM, examId)
                        .getOrThrow();

                if (examPrivileges != null && !examPrivileges.isEmpty()) {
                    final Collection<EntityPrivilege> result = new ArrayList<>(groupPrivileges);
                    result.addAll(examPrivileges);
                    return result;
                }
            }

            return groupPrivileges;

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
                    EntityType.SEB_GROUP,
                    Domain.CLIENT_ACCESS.ATTR_NAME,
                    "clientaccess:name:name.notunique");
        }
    }

}
