/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao.impl;

import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDAO;
import ch.ethz.seb.sps.utils.Constants;
import ch.ethz.seb.sps.utils.Result;
import io.minio.messages.DeleteObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

@Service
@ConditionalOnExpression("'${sps.data.store.adapter}'.equals('S3_RDBMS')")
public class ScreenshotDAOS3 implements ScreenshotDAO {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotDAOS3.class);
    private final S3DAO s3DAO;

    public ScreenshotDAOS3(final S3DAO s3DAO) {
        this.s3DAO = s3DAO;
    }

    @Override
    public Result<InputStream> getImage(
            final Long pk,
            final String sessionUUID) {

        return this.s3DAO.getItem(sessionUUID, pk);
    }

    @Override
    public Result<List<Long>> deleteAllForSession(
            final String sessionId,
            final List<Long> screenShotPKs) {

        return Result.tryCatch(() -> {
            this.s3DAO.deleteItemBatch(createItemListForDeletion(sessionId, screenShotPKs));
            return screenShotPKs;

        });
    }

    private List<DeleteObject> createItemListForDeletion(final String sessionId, final List<Long> screenShotPKs){
        List<DeleteObject> objects = new LinkedList<>();

        for (Long screenShotPK : screenShotPKs) {
            objects.add(new DeleteObject(sessionId + Constants.UNDERLINE + screenShotPK));
        }

        return objects;
    }
}