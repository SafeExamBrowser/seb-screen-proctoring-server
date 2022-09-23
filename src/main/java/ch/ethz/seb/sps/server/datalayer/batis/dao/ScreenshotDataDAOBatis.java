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

import ch.ethz.seb.sps.domain.model.screenshot.ScreenshotData;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordMapper;
import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataRecord;
import ch.ethz.seb.sps.server.servicelayer.dao.ScreenshotDataDAO;
import ch.ethz.seb.sps.utils.Result;

@Service
@ConditionalOnExpression("'${sps.data.store.adapter}'.equals('FULL_RDBMS') or '${sps.data.store.adapter}'.equals('FILESYS_RDBMS')")
public class ScreenshotDataDAOBatis implements ScreenshotDataDAO {

    private final ScreenshotDataRecordMapper screenshotDataRecordMapper;

    public ScreenshotDataDAOBatis(final ScreenshotDataRecordMapper screenshotDataRecordMapper) {
        this.screenshotDataRecordMapper = screenshotDataRecordMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Result<ScreenshotData> byPK(final Long id) {
        return Result.tryCatch(() -> this.screenshotDataRecordMapper.selectByPrimaryKey(id))
                .map(this::toDomainModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<ScreenshotData>> allOfSession(final String sessionUUID) {
        return Result.tryCatch(() -> this.screenshotDataRecordMapper.selectByExample()
                .where(ScreenshotDataRecordDynamicSqlSupport.sessionUuid, SqlBuilder.isEqualTo(sessionUUID))
                .build()
                .execute()
                .stream()
                .map(this::toDomainModel)
                .collect(Collectors.toList()));
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<ScreenshotData>> allLatestOfSessions(final String sessionIds) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Transactional(readOnly = false)
    public Result<Long> save(final ScreenshotData data) {
        return Result.tryCatch(() -> {
            final ScreenshotDataRecord screenshotData = new ScreenshotDataRecord(
                    null,
                    data.sessionUUID,
                    data.timestamp,
                    data.imageURL,
                    data.imageFormat,
                    data.metaData);
            this.screenshotDataRecordMapper.insert(screenshotData);
            return screenshotData.getId();
        });
    }

    private ScreenshotData toDomainModel(final ScreenshotDataRecord record) {
        return new ScreenshotData(
                record.getId(),
                record.getSessionUuid(),
                record.getTimestamp(),
                record.getImageUrl(),
                record.getImageFormat(),
                record.getMetaData());
    }

}
