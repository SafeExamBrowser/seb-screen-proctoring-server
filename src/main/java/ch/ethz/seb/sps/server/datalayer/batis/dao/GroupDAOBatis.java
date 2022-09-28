/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.batis.dao;

import org.mybatis.dynamic.sql.SqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.ethz.seb.sps.domain.model.screenshot.Group;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.GroupRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.GroupRecordMapper;
import ch.ethz.seb.sps.server.datalayer.batis.model.GroupRecord;
import ch.ethz.seb.sps.server.servicelayer.dao.GroupDAO;
import ch.ethz.seb.sps.utils.Result;
import ch.ethz.seb.sps.utils.Utils;

@Service
@ConditionalOnExpression("'${sps.data.store.adapter}'.equals('FULL_RDBMS') or '${sps.data.store.adapter}'.equals('FILESYS_RDBMS')")
public class GroupDAOBatis implements GroupDAO {

    private static final Logger log = LoggerFactory.getLogger(GroupDAOBatis.class);

    private final GroupRecordMapper groupRecordMapper;

    public GroupDAOBatis(final GroupRecordMapper groupRecordMapper) {
        this.groupRecordMapper = groupRecordMapper;
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
    @Transactional(readOnly = false)
    public Result<Group> createNew(final String groupUUID, final String name) {
        return Result.tryCatch(() -> {

            final GroupRecord groupRecord = new GroupRecord(
                    null,
                    groupUUID,
                    name,
                    Utils.getMillisecondsNow(),
                    null);

            this.groupRecordMapper.insertSelective(groupRecord);
            return groupRecord;
        })
                .map(this::toDomainModel)
                .onError(TransactionHandler::rollback);
    }

    @Override
    @Transactional(readOnly = false)
    public Result<Group> save(final Group data) {
        return Result.tryCatch(() -> {

            final GroupRecord groupRecord = new GroupRecord(
                    data.id,
                    data.uuid,
                    data.name,
                    null,
                    data.terminationTime);

            this.groupRecordMapper.updateByPrimaryKey(groupRecord);
            return this.groupRecordMapper.selectByPrimaryKey(data.id);
        })
                .map(this::toDomainModel)
                .onError(TransactionHandler::rollback);
    }

    private Group toDomainModel(final GroupRecord record) {
        return new Group(
                record.getId(),
                record.getUuid(),
                record.getName(),
                record.getCreationTime(),
                record.getTerminationTime());
    }

}
