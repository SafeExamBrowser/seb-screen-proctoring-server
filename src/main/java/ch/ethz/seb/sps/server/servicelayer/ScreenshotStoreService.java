/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer;

import java.io.InputStream;

import org.springframework.context.event.EventListener;

import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
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

    int getStoreHealthIndicator();
}
