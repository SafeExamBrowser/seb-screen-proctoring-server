/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao.impl;

import static org.mybatis.dynamic.sql.SqlBuilder.isIn;

import java.io.InputStream;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.ethz.seb.sps.server.ServiceInit;
import ch.ethz.seb.sps.server.ServiceInitEvent;
import ch.ethz.seb.sps.server.datalayer.batis.custommappers.ScreenshotMapper;
import ch.ethz.seb.sps.server.datalayer.batis.custommappers.ScreenshotMapper.BlobContent;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotRecordMapper;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDAO;
import ch.ethz.seb.sps.utils.Result;

@Service
@ConditionalOnExpression("'${sps.data.store.adapter}'.equals('FULL_RDBMS')")
public class ScreenshotDAOBatis implements ScreenshotDAO {

    private final ScreenshotMapper screenshotMapper;
    private final ScreenshotRecordMapper screenshotRecordMapper;

    public ScreenshotDAOBatis(
            final ScreenshotMapper screenshotMapper,
            final ScreenshotRecordMapper screenshotRecordMapper) {

        this.screenshotMapper = screenshotMapper;
        this.screenshotRecordMapper = screenshotRecordMapper;
    }

    @EventListener(ServiceInitEvent.class)
    public void init() {
        ServiceInit.INIT_LOGGER.info("----> Screenshot FULL_RDBMS Store: initialized");
    }

    @Override
    @Transactional(readOnly = true)
    public Result<InputStream> getImage(
            final Long pk,
            final String sessionUUID) {

        return Result.tryCatch(() -> {
            return this.screenshotMapper
                    .selectScreenshotByPK(pk)
                    .getImage();
        });
    }

    @Override
    @Transactional
    public Result<Long> storeImage(
            final Long pk,
            final String sessionUUID,
            final InputStream in) {

        return Result.tryCatch(() -> {
            final BlobContent blobContent = new BlobContent(pk, in);
            this.screenshotMapper.insert(blobContent);
            return blobContent.getId();
        });
    }

    @Override
    @Transactional
    public Result<List<Long>> deleteAllForSession(
            final String sessionId,
            final List<Long> screenShotPKs) {

        return Result.tryCatch(() -> {

            this.screenshotRecordMapper
                    .deleteByExample()
                    .where(ScreenshotRecordDynamicSqlSupport.id, isIn(screenShotPKs))
                    .build()
                    .execute();

            return screenShotPKs;
        })
                .onError(TransactionHandler::rollback);

    }

}
