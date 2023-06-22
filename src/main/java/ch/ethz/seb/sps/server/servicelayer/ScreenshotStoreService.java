/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer;

import java.io.InputStream;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import ch.ethz.seb.sps.domain.model.screenshot.Session.ImageFormat;
import ch.ethz.seb.sps.server.ServiceConfig;
import ch.ethz.seb.sps.server.ServiceInitEvent;

public interface ScreenshotStoreService {

    void storeScreenshot(
            String sessionUUID,
            Long timestamp,
            ImageFormat imageFormat,
            String metadata,
            InputStream in);

    @EventListener(ServiceInitEvent.class)
    void init();

    @Async(value = ServiceConfig.SCREENSHOT_UPLOAD_API_EXECUTOR)
    void storeScreenshot(String sessionUUID, InputStream in);

    int getStoreHealthIndicator();

}
