/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.batis.dao;

import java.util.Collection;
import java.util.stream.Collectors;

import org.mybatis.dynamic.sql.SqlBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.ethz.seb.sps.domain.model.screenshot.Session;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.SessionRecordDynamicSqlSupport;
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
    @Transactional(readOnly = true)
    public Result<Session> byPK(final Long id) {
        return Result.tryCatch(() -> this.sessionRecordMapper
                .selectByPrimaryKey(id))
                .map(this::toDomainModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<String>> allActiveSessionIds(final Long groupId) {
        return Result.tryCatch(() -> {
            return this.sessionRecordMapper.selectByExample()
                    .where(SessionRecordDynamicSqlSupport.groupId, SqlBuilder.isEqualTo(groupId))
                    .and(SessionRecordDynamicSqlSupport.terminationTime, SqlBuilder.isNull())
                    .build()
                    .execute()
                    .stream()
                    .map(rec -> rec.getUuid())
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional(readOnly = false)
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
    @Transactional(readOnly = false)
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

    @Override
    @Transactional(readOnly = false)
    public Result<String> closeSession(final String sessionUUID) {
        return Result.tryCatch(() -> {

            final Long id = this.sessionRecordMapper
                    .selectByExample()
                    .where(SessionRecordDynamicSqlSupport.uuid, SqlBuilder.isEqualTo(sessionUUID))
                    .build()
                    .execute()
                    .get(0)
                    .getId();

            final SessionRecord record = new SessionRecord(
                    id,
                    null,
                    null,
                    null,
                    null,
                    Utils.getMillisecondsNow());

            this.sessionRecordMapper.updateByPrimaryKeySelective(record);
            return sessionUUID;
        })
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
