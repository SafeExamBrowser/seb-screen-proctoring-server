/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import java.io.ByteArrayInputStream;

import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataRecord;

final class ScreenshotQueueData {

    final ScreenshotDataRecord record;
    final ByteArrayInputStream screenshotIn;

    public ScreenshotQueueData(
            final String sessionUUID,
            final Long timestamp,
            final ImageFormat imageFormat,
            final String metadata,
            final byte[] screenshot) {

        this.record = new ScreenshotDataRecord(
                null,
                sessionUUID,
                timestamp,
                imageFormat != null ? imageFormat.code : null,
                metadata);

        this.screenshotIn = new ByteArrayInputStream(screenshot);
    }
}