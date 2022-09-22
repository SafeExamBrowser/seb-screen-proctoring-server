/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.batis.dao;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.ethz.seb.sps.domain.model.screenshot.Group;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.GroupRecordMapper;
import ch.ethz.seb.sps.server.datalayer.batis.model.GroupRecord;
import ch.ethz.seb.sps.server.servicelayer.dao.GroupDAO;
import ch.ethz.seb.sps.utils.Result;

@Service
@ConditionalOnExpression("'${sps.data.store.adapter}'.equals('FULL_RDBMS') or '${sps.data.store.adapter}'.equals('FILESYS_RDBMS')")
public class GroupDAOBatis implements GroupDAO {

    private final GroupRecordMapper groupRecordMapper;

    public GroupDAOBatis(final GroupRecordMapper groupRecordMapper) {
        this.groupRecordMapper = groupRecordMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Group> byPK(final Long id) {
        return Result.tryCatch(() -> this.groupRecordMapper.selectByPrimaryKey(id))
                .map(this::toDomainModel);
    }

    @Override
    @Transactional(readOnly = false)
    public Result<Long> save(final Group data) {
        return Result.tryCatch(() -> {
            final GroupRecord groupRecord = new GroupRecord(null, data.uuid, data.name);
            this.groupRecordMapper.insert(groupRecord);
            return groupRecord.getId();
        });
    }

    private Group toDomainModel(final GroupRecord record) {
        return new Group(record.getId(), record.getUuid(), record.getName());
    }

}
