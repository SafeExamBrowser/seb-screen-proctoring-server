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
import com.google.common.collect.Lists;
import io.minio.messages.DeleteRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

@Service
@ConditionalOnExpression("'${sps.data.store.adapter}'.equals('S3_RDBMS')")
public class ScreenshotDAOS3 implements ScreenshotDAO {

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
            final String sessionUUID,
            final List<Long> screenShotPKs,
            final boolean wipe) {

        return Result.tryCatch(() -> {

            // NOTE: batch deletion only works for max 1000 Objects.
            //       if the list is longer we have to splice the request into several requests.
            //       https://github.com/minio/minio/issues/8247

            if (screenShotPKs.size() > 1000) {

                for (List<Long> partition : Lists.partition(screenShotPKs, 900)) {
                    this.s3DAO.deleteItemBatch(createItemListForDeletion(sessionUUID, partition));
                }

                return screenShotPKs;
            } else {
                this.s3DAO.deleteItemBatch(createItemListForDeletion(sessionUUID, screenShotPKs));
                return screenShotPKs;
            }
        });
    }

    private List<DeleteRequest.Object> createItemListForDeletion(
            final String sessionId,
            final List<Long> screenShotPKs) {

        List<DeleteRequest.Object> objects = new LinkedList<>();

        for (Long screenShotPK : screenShotPKs) {
            objects.add(new DeleteRequest.Object(sessionId + Constants.UNDERLINE + screenShotPK));
        }

        return objects;
    }
}