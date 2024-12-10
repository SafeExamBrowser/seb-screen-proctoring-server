package ch.ethz.seb.sps.server.datalayer.dao.impl;

import static ch.ethz.seb.sps.server.datalayer.batis.mapper.ExamRecordDynamicSqlSupport.examRecord;
import static ch.ethz.seb.sps.server.datalayer.batis.mapper.GroupRecordDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import java.util.*;
import java.util.stream.Collectors;

import ch.ethz.seb.sps.server.datalayer.dao.*;
import ch.ethz.seb.sps.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.Domain.EXAM;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.service.Exam;
import ch.ethz.seb.sps.domain.model.user.EntityPrivilege;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ExamRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ExamRecordMapper;
import ch.ethz.seb.sps.server.datalayer.batis.model.ExamRecord;
import ch.ethz.seb.sps.server.weblayer.BadRequestException;
import ch.ethz.seb.sps.utils.Result;
import ch.ethz.seb.sps.utils.Utils;

@Service
public class ExamDAOBatis implements ExamDAO, OwnedEntityDAO {

    private final ExamRecordMapper examRecordMapper;
    private final AdditionalAttributesDAO additionalAttributesDAO;
    private final GroupDAO groupDAO;
    private final EntityPrivilegeDAO entityPrivilegeDAO;

    public ExamDAOBatis(
            final ExamRecordMapper examRecordMapper,
            final AdditionalAttributesDAO additionalAttributesDAO,
            final GroupDAO groupDAO,
            final EntityPrivilegeDAO entityPrivilegeDAO) {

        this.examRecordMapper = examRecordMapper;
        this.additionalAttributesDAO = additionalAttributesDAO;
        this.groupDAO = groupDAO;
        this.entityPrivilegeDAO = entityPrivilegeDAO;
    }

    @Override
    public EntityType entityType() {
        return EntityType.EXAM;
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
    public Result<Exam> byPK(final Long id) {
        return recordByPK(id)
                .map(this::toDomainModel);
    }

    @Override
    public Result<Exam> byModelId(final String id) {
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
    public boolean existsByUUID(final String examUUID) {
        try {

            final Long count = this.examRecordMapper
                    .countByExample()
                    .where(ExamRecordDynamicSqlSupport.uuid, SqlBuilder.isEqualTo(examUUID))
                    .build()
                    .execute();

            return count != null && count.intValue() > 0;
        } catch (final Exception e) {
            log.error("Failed to check exam exists: ", e);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Set<Long>> getAllOwnedEntityPKs(final String userUUID) {
        return Result.tryCatch(() -> {
            return Utils.immutableSetOf(this.examRecordMapper
                    .selectIdsByExample()
                    .where(ExamRecordDynamicSqlSupport.owner, isEqualTo(userUUID))
                    .build()
                    .execute());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<Exam>> allOf(final Set<Long> pks) {
        return Result.tryCatch(() -> {

            if (pks == null || pks.isEmpty()) {
                return Collections.emptyList();
            }

            return this.examRecordMapper
                    .selectByExample()
                    .where(ExamRecordDynamicSqlSupport.id, isIn(new ArrayList<>(pks)))
                    .build()
                    .execute()
                    .stream()
                    .map(this::toDomainModel)
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<Exam>> allMatching(
            final FilterMap filterMap,
            final Collection<Long> prePredicated) {

        return Result.tryCatch(() -> {

            final Boolean active = filterMap.getBooleanObject(API.ACTIVE_FILTER);
            final Long fromTime = filterMap.getLong(API.PARAM_FROM_TIME);
            final Long toTime = filterMap.getLong(API.PARAM_TO_TIME);

            return this.examRecordMapper
                    .selectByExample()
                    .where(
                            ExamRecordDynamicSqlSupport.terminationTime,
                            (active != null) ? active ? SqlBuilder.isNull() : SqlBuilder.isNotNull()
                                    : SqlBuilder.isEqualToWhenPresent(() -> null))
                    .and(
                            ExamRecordDynamicSqlSupport.name,
                            isLikeWhenPresent(filterMap.getSQLWildcard(EXAM.ATTR_NAME)))
                    .and(
                            ExamRecordDynamicSqlSupport.description,
                            isLikeWhenPresent(filterMap.getSQLWildcard(EXAM.ATTR_DESCRIPTION)))
                    .and(
                            ExamRecordDynamicSqlSupport.creationTime,
                            SqlBuilder.isGreaterThanOrEqualToWhenPresent(fromTime))
                    .and(
                            ExamRecordDynamicSqlSupport.creationTime,
                            SqlBuilder.isLessThanOrEqualToWhenPresent(toTime))
                    .and(
                            ExamRecordDynamicSqlSupport.id,
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
    public Result<Collection<Exam>> getExamsStarted(final FilterMap filterMap) {

        return Result.tryCatch(() -> {

            final Long fromTime = filterMap.getLong(API.PARAM_FROM_TIME);
            final Long toTime = filterMap.getLong(API.PARAM_TO_TIME);

            return this.examRecordMapper
                    .selectByExample()
                    .where(
                            ExamRecordDynamicSqlSupport.startTime,
                            SqlBuilder.isGreaterThanOrEqualToWhenPresent(fromTime))
                    .and(
                            ExamRecordDynamicSqlSupport.startTime,
                            SqlBuilder.isLessThanOrEqualToWhenPresent(toTime))
                    .build()
                    .execute()
                    .stream()
                    .map(this::toDomainModel)
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<Long>> getAllForDeletion() {
        return Result.tryCatch(() -> {
            long now = Utils.getMillisecondsNow();
            return this.examRecordMapper
                    .selectIdsByExample()
                    .where(
                            ExamRecordDynamicSqlSupport.deletionTime, 
                            SqlBuilder.isNotNull())
                    .and(
                            ExamRecordDynamicSqlSupport.deletionTime,
                            SqlBuilder.isLessThanOrEqualTo(now))
                    .build()
                    .execute();
        });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasRunningLifeExams() {
        try {
            long now = Utils.getMillisecondsNow();
            return !this.examRecordMapper
                    .selectByExample()
                    .where(
                            ExamRecordDynamicSqlSupport.endTime,
                            SqlBuilder.isNotNull())
                    .and(ExamRecordDynamicSqlSupport.endTime,
                            SqlBuilder.isGreaterThan(now))
                    .and(ExamRecordDynamicSqlSupport.startTime,
                            SqlBuilder.isLessThanOrEqualTo((now)))
                    .build()
                    .execute()
                    .isEmpty();
            
        } catch (Exception e) {
            log.error("Failed to check if any running life exam exists: ", e);
            // If we are not sure if at least one life running exam exists, we assume it does.
            return true;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Set<Long>> getAllOwnedIds(final String userUUID) {
        return Result.tryCatch(() -> {
            final List<Long> result = this.examRecordMapper
                    .selectIdsByExample()
                    .where(
                            ExamRecordDynamicSqlSupport.owner,
                            SqlBuilder.isEqualTo(userUUID))
                    .build()
                    .execute();

            return Utils.immutableSetOf(result);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<Exam>> pksByExamName(final FilterMap filterMap) {
        return Result.tryCatch(() -> {
            return this.examRecordMapper
                    .selectByExample()
                    .where(
                            ExamRecordDynamicSqlSupport.terminationTime, SqlBuilder.isNull())
                    .and(
                            ExamRecordDynamicSqlSupport.name,
                            isLikeWhenPresent(filterMap.getSQLWildcard(API.PARAM_EXAM_NAME)))
                    .build()
                    .execute()
                    .stream()
                    .map(this::toDomainModel)
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExamRunning(Long examId) {
        // Note: Since start-date and end-date are just informational, in SPS
        //       the termination_time indicated whether an Exam is running or not.
        //       On SEB Server there are some configurable pre- and post time slices applied to start date and end date
        //       that means these dates do not indicate a running Exam in all cases. In contrary the termination_time
        //       can be controlled by SEB Server (or another service) independently of LMS start- and end-time
        try {

            Long count = this.examRecordMapper
                    .countByExample()
                    .where(
                            ExamRecordDynamicSqlSupport.terminationTime, isNull())
                    .and(
                            ExamRecordDynamicSqlSupport.id,
                            isEqualTo(examId))
                    .build()
                    .execute();

            return count >= 1;

        } catch (Exception e) {
            log.error("Failed to verify if exam is running: ", e);
            return false;
        }

    }

    @Override
    @Transactional(readOnly = true)
    public boolean isActive(final String modelId) {
        if (StringUtils.isBlank(modelId)) {
            return false;
        }

        return this.examRecordMapper
                .countByExample()
                .where(ExamRecordDynamicSqlSupport.id, isEqualTo(Long.valueOf(modelId)))
                .and(ExamRecordDynamicSqlSupport.terminationTime, SqlBuilder.isNull())
                .build()
                .execute() > 0;
    }

    @Override
    @Transactional
    public Result<EntityKey> setActive(final EntityKey entityKey, final boolean active) {
        return pkByUUID(entityKey.modelId)
                .map(pk -> {

                    final long now = Utils.getMillisecondsNow();

                    UpdateDSL.updateWithMapper(this.examRecordMapper::update, examRecord)
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
    public Result<Exam> createNew(final Exam data) {
        return Result.tryCatch(() -> {

            if (data.uuid != null) {
                checkUniqueUUID(data);
            }

            final String supporter = !data.supporter.isEmpty() 
                    ? StringUtils.join(data.supporter, Constants.LIST_SEPARATOR) 
                    : null;

            final long millisecondsNow = Utils.getMillisecondsNow();
            final ExamRecord newRecord = new ExamRecord(
                    null,
                    (StringUtils.isNotBlank(data.uuid)) ? data.uuid : UUID.randomUUID().toString(),
                    data.name,
                    data.description,
                    data.url,
                    data.type,
                    data.owner,
                    supporter,
                    millisecondsNow,
                    millisecondsNow,
                    null,
                    data.startTime != null ? data.startTime : millisecondsNow,
                    data.endTime,
                    data.deletionTime);

            this.examRecordMapper.insert(newRecord);

            return this.examRecordMapper.selectByPrimaryKey(newRecord.getId());
        })
                .map(this::toDomainModel)
                .onError(TransactionHandler::rollback);
    }

    @Override
    @Transactional
    public Result<Exam> save(final Exam data) {
        return Result.tryCatch(() -> {

            final long millisecondsNow = Utils.getMillisecondsNow();

            Long pk = data.id;
            if (pk == null && data.uuid != null) {
                pk = this.pkByUUID(data.uuid).getOr(null);
            }
            if (pk == null) {
                throw new BadRequestException("exam save", "no exam with uuid: " + data.uuid + "found");
            }

            final String supporter = !data.supporter.isEmpty()
                    ? StringUtils.join(data.supporter, Constants.LIST_SEPARATOR)
                    : null;

            UpdateDSL.updateWithMapper(this.examRecordMapper::update, examRecord)
                    .set(ExamRecordDynamicSqlSupport.name).equalTo(data.name)
                    .set(ExamRecordDynamicSqlSupport.description).equalTo(data.description)
                    .set(ExamRecordDynamicSqlSupport.url).equalTo(data.url)
                    .set(ExamRecordDynamicSqlSupport.type).equalTo(data.type)
                    .set(ExamRecordDynamicSqlSupport.supporter).equalToWhenPresent(supporter)
                    .set(ExamRecordDynamicSqlSupport.startTime).equalTo(data.startTime)
                    .set(ExamRecordDynamicSqlSupport.endTime).equalTo(data.endTime)
                    .set(ExamRecordDynamicSqlSupport.lastUpdateTime).equalTo(millisecondsNow)
                    .set(ExamRecordDynamicSqlSupport.deletionTime).equalTo(data.deletionTime)
                    .where(ExamRecordDynamicSqlSupport.id, isEqualTo(pk))
                    .build()
                    .execute();
            
            return this.examRecordMapper.selectByPrimaryKey(pk);
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

            // delete all involved groups first
            final Collection<EntityKey> deletedGroups = this.groupDAO
                    .deleteAllForExams(ids)
                    .getOrThrow();

            log.info("Deleted following groups: {} before deleting exams: {}", deletedGroups, all);

            // delete all additional attributes
            ids.forEach(id -> this.additionalAttributesDAO.deleteAll(EntityType.EXAM, id));

            // delete all involved entity privileges
            deleteAllEntityPrivileges(ids, this.entityPrivilegeDAO);

            // delete the exams
            this.examRecordMapper
                    .deleteByExample()
                    .where(ExamRecordDynamicSqlSupport.id, isIn(ids))
                    .build()
                    .execute();

            return ids.stream()
                    .map(pk -> new EntityKey(pk, EntityType.EXAM))
                    .collect(Collectors.toList());
        });
    }

    private Result<ExamRecord> recordByUUID(final String uuid) {
        return Result.tryCatch(() -> {

            final List<ExamRecord> execute = this.examRecordMapper.selectByExample()
                    .where(ExamRecordDynamicSqlSupport.uuid, isEqualTo(uuid))
                    .build()
                    .execute();

            if (execute == null || execute.isEmpty()) {
                throw new NoResourceFoundException(EntityType.EXAM, uuid);
            }

            return execute.get(0);
        });
    }

    private Result<Long> pkByUUID(final String examUUID) {

        return Result.tryCatch(() -> {
            final List<Long> execute = this.examRecordMapper
                    .selectIdsByExample()
                    .where(ExamRecordDynamicSqlSupport.uuid, SqlBuilder.isEqualTo(examUUID))
                    .build()
                    .execute();

            if (execute == null || execute.isEmpty()) {
                throw new NoResourceFoundException(EntityType.EXAM, examUUID);
            }

            return execute.get(0);
        });
    }

    private Result<ExamRecord> recordByPK(final Long pk) {
        return Result.tryCatch(() -> {

            final ExamRecord selectByPrimaryKey = this.examRecordMapper.selectByPrimaryKey(pk);

            if (selectByPrimaryKey == null) {
                throw new NoResourceFoundException(EntityType.EXAM, String.valueOf(pk));
            }

            return selectByPrimaryKey;
        });
    }

    private Exam toDomainModel(final ExamRecord record) {

//        List<String> userIds = this.additionalAttributesDAO
//                .getAdditionalAttribute(EntityType.EXAM, record.getId(), Exam.ATTR_USER_IDS)
//                .map(AdditionalAttributeRecord::getValue)
//                .map(ids -> Arrays.asList(StringUtils.split(ids, Constants.LIST_SEPARATOR)))
//                .getOr(Collections.emptyList());

        final List<String> supporter = StringUtils.isNotBlank(record.getSupporter()) 
                ? Arrays.asList(StringUtils.split(record.getSupporter(), Constants.LIST_SEPARATOR)) 
                : null;
        
        return new Exam(
                record.getId(),
                record.getUuid(),
                record.getName(),
                record.getDescription(),
                record.getUrl(),
                record.getType(),
                record.getOwner(),
                supporter,
                record.getCreationTime(),
                record.getLastUpdateTime(),
                record.getTerminationTime(),
                record.getStartTime(),
                record.getEndTime(),
                record.getDeletionTime(),
                getEntityPrivileges(record.getId()));
    }

    private Collection<EntityPrivilege> getEntityPrivileges(final Long id) {
        try {

            if (id == null) {
                return Collections.emptyList();
            }

            return this.entityPrivilegeDAO
                    .getEntityPrivileges(EntityType.EXAM, id)
                    .getOrThrow();

        } catch (final Exception e) {
            log.error("Failed to get entity privileges for Exam: {}", id, e);
            return Collections.emptyList();
        }
    }

    private void checkUniqueUUID(final Exam exam) {

        final Long otherWithSameName = this.examRecordMapper
                .countByExample()
                .where(ExamRecordDynamicSqlSupport.uuid, isEqualTo(exam.uuid))
                .and(ExamRecordDynamicSqlSupport.id, isNotEqualToWhenPresent(exam.id))
                .build()
                .execute();

        if (otherWithSameName != null && otherWithSameName > 0) {
            throw new DuplicateEntityException(
                    EntityType.EXAM,
                    Domain.CLIENT_ACCESS.ATTR_UUID,
                    "exam:uuid.notunique");
        }
    }

}
