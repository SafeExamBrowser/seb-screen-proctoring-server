/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao.impl;

import ch.ethz.seb.sps.server.datalayer.batis.custommappers.ScreenshotMapper;
import ch.ethz.seb.sps.server.datalayer.batis.custommappers.ScreenshotMapper.BlobContent;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotRecordMapper;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDAO;
import ch.ethz.seb.sps.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

import static org.mybatis.dynamic.sql.SqlBuilder.isIn;

@Service
@ConditionalOnExpression("'${sps.data.store.adapter}'.equals('S3')")
public class ScreenshotDAOS3 implements ScreenshotDAO {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotDAOS3.class);

    private final ScreenshotMapper screenshotMapper;
    private final ScreenshotRecordMapper screenshotRecordMapper;
    private final S3DAO s3DAO;


    public ScreenshotDAOS3(
            final ScreenshotMapper screenshotMapper,
            final ScreenshotRecordMapper screenshotRecordMapper,
            final S3DAO s3DAO) {

        this.screenshotMapper = screenshotMapper;
        this.screenshotRecordMapper = screenshotRecordMapper;
        this.s3DAO = s3DAO;
    }

    @Override
    @Transactional(readOnly = true)
    public Result<InputStream> getImage(
            final Long pk,
            final String sessionUUID) {

        return this.s3DAO.getItem(sessionUUID, pk)
                .onError(error -> log.error("Failed to retrieve screenshot from S3 service: ", error));
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
