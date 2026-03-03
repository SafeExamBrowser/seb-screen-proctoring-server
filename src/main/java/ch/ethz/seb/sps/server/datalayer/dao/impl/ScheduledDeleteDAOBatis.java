package ch.ethz.seb.sps.server.datalayer.dao.impl;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.JSONMapper;
import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.service.ScheduledDelete;
import ch.ethz.seb.sps.domain.model.service.ScheduledDeleteInfo;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.*;
import ch.ethz.seb.sps.server.datalayer.batis.model.ScheduledDeleteInfoRecord;
import ch.ethz.seb.sps.server.datalayer.batis.model.ScheduledDeleteRecord;
import ch.ethz.seb.sps.server.datalayer.dao.NoResourceFoundException;
import ch.ethz.seb.sps.server.datalayer.dao.ScheduledDeleteDAO;
import ch.ethz.seb.sps.utils.Result;
import ch.ethz.seb.sps.utils.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
public class ScheduledDeleteDAOBatis implements ScheduledDeleteDAO {

    private static final Logger log = LoggerFactory.getLogger(ScheduledDeleteDAOBatis.class);

    private final ScheduledDeleteRecordMapper scheduledDeleteRecordMapper;
    private final ScheduledDeleteInfoRecordMapper scheduledDeleteInfoRecordMapper;
    private final JSONMapper jsonMapper;


    public ScheduledDeleteDAOBatis(
            final ScheduledDeleteRecordMapper scheduledDeleteRecordMapper,
            final ScheduledDeleteInfoRecordMapper scheduledDeleteInfoRecordMapper,
            final JSONMapper jsonMapper) {

        this.scheduledDeleteRecordMapper = scheduledDeleteRecordMapper;
        this.scheduledDeleteInfoRecordMapper = scheduledDeleteInfoRecordMapper;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public EntityType entityType() {
        return EntityType.SCHEDULED_DELETE;
    }

    @Override
    @Transactional(readOnly = true)
    public Result<ScheduledDelete> byPK(final Long id) {
        return recordByPK(id)
                .map(this::toDomainModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<ScheduledDelete>> allOf(final Set<Long> pks) {
        return Result.tryCatch(() -> {

            if (pks == null || pks.isEmpty()) {
                return Collections.emptyList();
            }

            return this.scheduledDeleteRecordMapper
                    .selectByExample()
                    .where(ScheduledDeleteRecordDynamicSqlSupport.id, isIn(new ArrayList<>(pks)))
                    .build()
                    .execute()
                    .stream()
                    .map(this::toDomainModel)
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<ScheduledDelete>> allMatching(
            final FilterMap filterMap,
            final Collection<Long> prePredicated) {

        return Result.tryCatch(() -> this.scheduledDeleteRecordMapper
                .selectByExample()
                .where(
                        ScheduledDeleteRecordDynamicSqlSupport.state,
                        SqlBuilder.isEqualToWhenPresent(filterMap.getString(Domain.SCHEDULED_DELETE.TYPE_NAME)))
                .and(
                        ScheduledDeleteRecordDynamicSqlSupport.deleteDueTime,
                        SqlBuilder.isLessThanOrEqualToWhenPresent(filterMap.getLong(Domain.SCHEDULED_DELETE.ATTR_DELETE_DUE_TIME)))
                .build()
                .execute()
                .stream()
                .map(this::toDomainModel)
                .collect(Collectors.toList()));
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Set<Long>> getAllOwnedIds(String userUUID) {
        return Result.tryCatch(() -> {
            final List<Long> result = this.scheduledDeleteRecordMapper
                    .selectIdsByExample()
                    .where(
                            ScheduledDeleteRecordDynamicSqlSupport.owner,
                            SqlBuilder.isEqualTo(userUUID))
                    .build()
                    .execute();

            return Utils.immutableSetOf(result);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<ScheduledDelete>> getSchedulesReadyForProcessing() {
        return Result.tryCatch(() -> {
            return this.scheduledDeleteRecordMapper
                    .selectByExample()
                    .where(
                            ScheduledDeleteRecordDynamicSqlSupport.state,
                            SqlBuilder.isEqualTo(ScheduledDelete.State.PENDING.name()))
                    .and(
                            ScheduledDeleteRecordDynamicSqlSupport.deleteDueTime,
                            SqlBuilder.isLessThanOrEqualTo(Utils.getMillisecondsNow()))
                    .build()
                    .execute()
                    .stream()
                    .map(this::toDomainModel)
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional
    public Result<ScheduledDelete> createNew(ScheduledDelete data) {
        return Result.tryCatch(() -> {

            // first create the ScheduledDelete entry
            ScheduledDeleteRecord scheduledDeleteRecord = new ScheduledDeleteRecord(
                    null,
                    ScheduledDelete.State.PENDING.name(),
                    data.deleteDueTime(),
                    data.scheduleTime(),
                    null,
                    null,
                    data.ownerUUID()
            );
            this.scheduledDeleteRecordMapper.insert(scheduledDeleteRecord);

            // add collected infos
            Collection<ScheduledDeleteInfo> infos = data.info();
            if (infos != null) {
                infos.forEach( info -> {
                        this.scheduledDeleteInfoRecordMapper.insert(new ScheduledDeleteInfoRecord(
                                null,
                                scheduledDeleteRecord.getId(),
                                info.state() != null ? info.state().name() : ScheduledDeleteInfo.State.PENDING.name(),
                                info.examUUID(),
                                putDeletionInfo(info),
                                info.errorInfo()
                        ));
                });
            }

            return scheduledDeleteRecordMapper.selectByPrimaryKey(scheduledDeleteRecord.getId());

        })
                .map(this::toDomainModel)
                .onError(TransactionHandler::rollback);
    }

    @Override
    @Transactional
    public Result<ScheduledDelete> addInfo(
            final Long scheduledDeleteId,
            final Collection<ScheduledDeleteInfo> info) {

        return Result.tryCatch(() -> {
            if (info != null && !info.isEmpty()) {
                info.forEach(infoData ->  {
                    this.scheduledDeleteInfoRecordMapper.insert(new ScheduledDeleteInfoRecord(
                            null,
                            scheduledDeleteId,
                            ScheduledDeleteInfo.State.PENDING.toString(),
                            putDeletionInfo(infoData),
                            infoData.examUUID(),
                            null
                    ));
                });
            }

            return scheduledDeleteRecordMapper.selectByPrimaryKey(scheduledDeleteId);
        })
                .map(this::toDomainModel)
                .onError(TransactionHandler::rollback);
    }

    @Override
    @Transactional
    public Result<ScheduledDelete> save(ScheduledDelete data) {
        return Result.ofRuntimeError("Unsupported Operation Save ScheduledDelete");
    }

    @Override
    @Transactional
    public void startProcessing(final Long deleteId) {
        try {

            final long now = Utils.getMillisecondsNow();
            UpdateDSL.updateWithMapper(this.scheduledDeleteRecordMapper::update, ScheduledDeleteRecordDynamicSqlSupport.scheduledDeleteRecord)
                    .set(ScheduledDeleteRecordDynamicSqlSupport.startTime).equalTo(now)
                    .set(ScheduledDeleteRecordDynamicSqlSupport.state).equalTo(ScheduledDelete.State.RUNNING.name())
                    .where(ScheduledDeleteRecordDynamicSqlSupport.id, isEqualTo(deleteId))
                    .build()
                    .execute();

        } catch (Exception e) {
            log.error("Failed to mark scheduled delete as running: {} cause: {}", deleteId, e.getMessage());
        }
    }

    @Override
    @Transactional
    public void endProcessing(final Long deleteId) {
        try {

            final long now = Utils.getMillisecondsNow();
            UpdateDSL.updateWithMapper(this.scheduledDeleteRecordMapper::update, ScheduledDeleteRecordDynamicSqlSupport.scheduledDeleteRecord)
                    .set(ScheduledDeleteRecordDynamicSqlSupport.endTime).equalTo(now)
                    .set(ScheduledDeleteRecordDynamicSqlSupport.state).equalTo(ScheduledDelete.State.FINISHED.name())
                    .where(ScheduledDeleteRecordDynamicSqlSupport.id, isEqualTo(deleteId))
                    .build()
                    .execute();

        } catch (Exception e) {
            log.error("Failed to mark scheduled delete as finished: {} cause: {}", deleteId, e.getMessage());
        }
    }

    @Override
    @Transactional
    public void startSingleDeletion(final Long infoId) {
        try {

            UpdateDSL.updateWithMapper(this.scheduledDeleteInfoRecordMapper::update, ScheduledDeleteInfoRecordDynamicSqlSupport.scheduledDeleteInfoRecord)
                    .set(ScheduledDeleteInfoRecordDynamicSqlSupport.state).equalTo(ScheduledDeleteInfo.State.RUNNING.name())
                    .where(ScheduledDeleteInfoRecordDynamicSqlSupport.id, isEqualTo(infoId))
                    .build()
                    .execute();

        } catch (Exception e) {
            log.error("Failed to mark scheduled delete exam as running: {} cause: {}", infoId, e.getMessage());
        }
    }

    @Override
    @Transactional
    public void endSingleDeletion(final Long infoId, final String errorInfo) {
        try {

            UpdateDSL.updateWithMapper(this.scheduledDeleteInfoRecordMapper::update, ScheduledDeleteInfoRecordDynamicSqlSupport.scheduledDeleteInfoRecord)
                    .set(ScheduledDeleteInfoRecordDynamicSqlSupport.state).equalTo(
                            StringUtils.isBlank(errorInfo)
                                ? ScheduledDeleteInfo.State.DELETED.name()
                                : ScheduledDeleteInfo.State.ERROR.name())
                    .set(ScheduledDeleteInfoRecordDynamicSqlSupport.errorInfo).equalToWhenPresent(errorInfo)
                    .where(ScheduledDeleteInfoRecordDynamicSqlSupport.id, isEqualTo(infoId))
                    .build()
                    .execute();

        } catch (Exception e) {
            log.error("Failed to mark scheduled delete exam as finished: {} cause: {}", infoId, e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<Collection<EntityKey>> delete(Set<EntityKey> all) {
        return Result.tryCatch(() -> extractListOfPKs(all))
                .map(this::delete);
    }



    private Result<ScheduledDeleteRecord> recordByPK(final Long pk) {
        return Result.tryCatch(() -> {

            final ScheduledDeleteRecord selectByPrimaryKey = this.scheduledDeleteRecordMapper.selectByPrimaryKey(pk);

            if (selectByPrimaryKey == null) {
                throw new NoResourceFoundException(EntityType.SCHEDULED_DELETE, String.valueOf(pk));
            }

            return selectByPrimaryKey;
        });
    }

    private Result<Collection<ScheduledDeleteInfoRecord>> infoByPK(final Long pk) {
        return Result.tryCatch(() -> this.scheduledDeleteInfoRecordMapper
                .selectByExample()
                .where(ScheduledDeleteInfoRecordDynamicSqlSupport.scheduledDeleteId, SqlBuilder.isEqualTo(pk))
                .build()
                .execute());
    }

    private ScheduledDelete toDomainModel(final ScheduledDeleteRecord rec) {
        Collection<ScheduledDeleteInfo> info = infoByPK(rec.getId())
                .getOrThrow()
                .stream()
                .map(info_rec ->
                        new ScheduledDeleteInfo(
                                info_rec.getId(),
                                info_rec.getScheduledDeleteId(),
                                ScheduledDeleteInfo.State.valueOf(info_rec.getState()),
                                info_rec.getExamUuid(),
                                getDeletionInfo(info_rec),
                                info_rec.getErrorInfo()
                        ))
                .toList();

        return new ScheduledDelete(
                rec.getId(),
                ScheduledDelete.State.valueOf(rec.getState()),
                rec.getDeleteDueTime(),
                rec.getScheduleTime(),
                rec.getStartTime(),
                rec.getEndTime(),
                rec.getOwner(),
                info
        );
    }



    private Collection<EntityKey> delete(final List<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        // first delete all infos
        this.scheduledDeleteInfoRecordMapper
                .deleteByExample()
                .where(ScheduledDeleteInfoRecordDynamicSqlSupport.scheduledDeleteId, isIn(ids))
                .build()
                .execute();

        // then delete the nodes
        this.scheduledDeleteRecordMapper
                .deleteByExample()
                .where(ScheduledDeleteRecordDynamicSqlSupport.id, isIn(ids))
                .build()
                .execute();

        return ids.stream()
                .map(pk -> new EntityKey(pk, EntityType.SCHEDULED_DELETE))
                .collect(Collectors.toList());
    }

    private Map<String, String>  getDeletionInfo(final ScheduledDeleteInfoRecord rec) {
        try {
            String delInfo = rec.getDeletionInfo();
            if (StringUtils.isBlank(delInfo)) {
                return Collections.emptyMap();
            }
            return  jsonMapper.readValue(delInfo, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("Failed to parse deletion info from: {} cause: {}", rec.getDeletionInfo(), e.getMessage());
            return Collections.emptyMap();
        }
    }

    private String putDeletionInfo(final ScheduledDeleteInfo info) {
        try {
            Map<String, String> delInfo = info.deletionInfo();
            if (delInfo == null || delInfo.isEmpty()) {
                return null;
            }

            return jsonMapper.writeValueAsString(delInfo);
        } catch (Exception e) {
            log.error("Failed to serialize deletion info for: {} cause: {}", info.deletionInfo(), e.getMessage());
            return null;
        }
    }
}
