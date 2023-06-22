/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDAO;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDataDAO;
import ch.ethz.seb.sps.server.servicelayer.ScreenshotFetchService;

@Lazy
@Component
public class ScreenshotFetchServiceImpl implements ScreenshotFetchService {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotFetchServiceImpl.class);

    private final ScreenshotDAO screenshotDAO;
    private final ScreenshotDataDAO screenshotDataDAO;

    public ScreenshotFetchServiceImpl(
            final ScreenshotDAO screenshotDAO,
            final ScreenshotDataDAO screenshotDataDAO) {

        this.screenshotDAO = screenshotDAO;
        this.screenshotDataDAO = screenshotDataDAO;
    }

    @Override
    public void streamLatestScreenshot(
            final String sessionId,
            final OutputStream out) {

        try {

            final InputStream screenshotIn = this.screenshotDataDAO.getLatestScreenshotId(sessionId)
                    .flatMap(screenshotId -> this.screenshotDAO.getImage(screenshotId, sessionId))
                    .getOrThrow();

            IOUtils.copy(screenshotIn, out);

        } catch (final Exception e) {
            log.error("Failed to get latest screenshot image: ", e);
        }
    }

}
