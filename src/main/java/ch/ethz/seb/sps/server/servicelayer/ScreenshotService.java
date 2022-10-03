/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;

import ch.ethz.seb.sps.server.ServiceConfig;

public interface ScreenshotService {

    @Async(value = ServiceConfig.SCREENSHOT_UPLOAD_API_EXECUTOR)
    public void storeScreenshot(
            final String sessionUUID,
            final Long timestamp,
            final String format,
            final String metadata,
            final InputStream in,
            CompletableFuture<Void> completableFuture);

    @Async(value = ServiceConfig.SCREENSHOT_DOWNLOAD_API_EXECUTOR)
    public void streamLatestScreenshot(
            final String sessionId,
            OutputStream out);

}
