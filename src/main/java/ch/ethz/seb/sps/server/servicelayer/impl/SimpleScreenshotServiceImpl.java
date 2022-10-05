/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.ethz.seb.sps.domain.model.screenshot.ScreenshotData;
import ch.ethz.seb.sps.server.servicelayer.ScreenshotService;
import ch.ethz.seb.sps.server.servicelayer.dao.ScreenshotDAO;
import ch.ethz.seb.sps.server.servicelayer.dao.ScreenshotDataDAO;

@Service
public class SimpleScreenshotServiceImpl implements ScreenshotService {

    private static final Logger log = LoggerFactory.getLogger(SimpleScreenshotServiceImpl.class);

    private final ScreenshotDAO screenshotDAO;
    private final ScreenshotDataDAO screenshotDataDAO;

    public SimpleScreenshotServiceImpl(
            final ScreenshotDAO screenshotDAO,
            final ScreenshotDataDAO screenshotDataDAO) {

        this.screenshotDAO = screenshotDAO;
        this.screenshotDataDAO = screenshotDataDAO;
    }

    @Override
    public void storeScreenshot(
            final String sessionId,
            final Long timestamp,
            final String format,
            final String metadata,
            final InputStream in,
            final CompletableFuture<Void> completableFuture) {

        this.screenshotDataDAO
                .save(new ScreenshotData(null, sessionId, timestamp, null, format, metadata))
                .onSuccess(id -> this.screenshotDAO
                        .storeImage(id, sessionId, in)
                        .onError(error -> log.error("Failed to store screen shot: ", error)))
                .onError(error -> log.error("Failed to store screen shot data: ", error));

        completableFuture.complete(null);
    }

    @Override
    public void streamLatestScreenshot(
            final String sessionId,
            final OutputStream out,
            final CompletableFuture<Void> completableFuture) {
        try {

            final InputStream screenshotIn = this.screenshotDataDAO.getLatestScreenshotId(sessionId)
                    .flatMap(screenshotId -> this.screenshotDAO.getImage(screenshotId, sessionId))
                    .getOrThrow();

            IOUtils.copy(screenshotIn, out);

        } catch (final Exception e) {
            log.error("Failed to get latest screenshot image: ", e);
        } finally {
            completableFuture.complete(null);
        }
    }

}
