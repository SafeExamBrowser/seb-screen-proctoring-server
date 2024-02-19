/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao.impl;

import ch.ethz.seb.sps.server.ServiceInit;
import ch.ethz.seb.sps.server.ServiceInitEvent;
import ch.ethz.seb.sps.server.datalayer.batis.custommappers.ScreenshotMapper;
import ch.ethz.seb.sps.server.datalayer.batis.custommappers.ScreenshotMapper.BlobContent;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotRecordMapper;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDAO;
import ch.ethz.seb.sps.utils.Result;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

import static org.mybatis.dynamic.sql.SqlBuilder.isIn;

@Service
@ConditionalOnExpression("'${sps.data.store.adapter}'.equals('S3')")
public class ScreenshotDAOS3 implements ScreenshotDAO {

    private final ScreenshotMapper screenshotMapper;
    private final ScreenshotRecordMapper screenshotRecordMapper;
    private final Environment environment;
    private MinioClient minioClient;


    public ScreenshotDAOS3(
            final ScreenshotMapper screenshotMapper,
            final ScreenshotRecordMapper screenshotRecordMapper,
            final Environment environment) {

        this.screenshotMapper = screenshotMapper;
        this.screenshotRecordMapper = screenshotRecordMapper;
        this.environment = environment;
    }

    @EventListener(ServiceInitEvent.class)
    public void init() {
        ServiceInit.INIT_LOGGER.info("----> Screenshot S3 Store: initialized");

        this.minioClient =
                MinioClient.builder()
                        .endpoint(this.environment.getProperty("sps.s3.endpointUrl"))
                        .credentials(this.environment.getProperty("sps.s3.accessKey"), this.environment.getProperty("sps.s3.secretKey"))
                        .build();

    }

    @Override
    @Transactional(readOnly = true)
    public Result<InputStream> getImage(
            final Long pk,
            final String sessionUUID) {

        //sessionUUID, timestamp
        //d7080e42-3e7a-4b80-b523-fccaadbafcc8_1707488185984

        System.out.println(sessionUUID + "_" + pk);

        return Result.tryCatch(() -> {
            return minioClient.getObject(
                    GetObjectArgs
                            .builder()
                            .bucket("sebserver-dev")
                            .object(sessionUUID + "_" + pk)
                            .build()
            );
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
