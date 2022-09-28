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

import ch.ethz.seb.sps.domain.model.screenshot.Session;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.SessionRecordMapper;
import ch.ethz.seb.sps.server.datalayer.batis.model.SessionRecord;
import ch.ethz.seb.sps.server.servicelayer.dao.SessionDAO;
import ch.ethz.seb.sps.utils.Result;
import ch.ethz.seb.sps.utils.Utils;

@Service
@ConditionalOnExpression("'${sps.data.store.adapter}'.equals('FULL_RDBMS') or '${sps.data.store.adapter}'.equals('FILESYS_RDBMS')")
public class SessionDAOBatis implements SessionDAO {

    private final SessionRecordMapper sessionRecordMapper;

    public SessionDAOBatis(final SessionRecordMapper sessionRecordMapper) {
        this.sessionRecordMapper = sessionRecordMapper;
    }

    @Override
    public Result<Session> byPK(final Long id) {
        return Result.tryCatch(() -> this.sessionRecordMapper
                .selectByPrimaryKey(id))
                .map(this::toDomainModel);
    }

    @Override
    public Result<Session> createNew(final Long groupId, final String uuid, final String name) {
        return Result.tryCatch(() -> {

            final SessionRecord record = new SessionRecord(
                    null,
                    groupId,
                    uuid,
                    name,
                    Utils.getMillisecondsNow(),
                    null);

            this.sessionRecordMapper.insert(record);
            return record;
        })
                .map(this::toDomainModel)
                .onError(TransactionHandler::rollback);
    }

    @Override
    public Result<Session> save(final Session data) {
        return Result.tryCatch(() -> {

            final SessionRecord record = new SessionRecord(
                    data.id,
                    data.groupId,
                    data.uuid,
                    data.name,
                    null,
                    data.terminationTime);

            this.sessionRecordMapper.updateByPrimaryKey(record);
            return this.sessionRecordMapper.selectByPrimaryKey(data.id);
        })
                .map(this::toDomainModel)
                .onError(TransactionHandler::rollback);
    }

    private Session toDomainModel(final SessionRecord record) {
        return new Session(
                record.getId(),
                record.getGroupId(),
                record.getUuid(),
                record.getName(),
                record.getCreationTime(),
                record.getTerminationTime());
    }

}
