/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import java.io.InputStream;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ch.ethz.seb.sps.domain.model.screenshot.Session.ImageFormat;
import ch.ethz.seb.sps.server.servicelayer.ScreenshotStoreService;

@Lazy
@Component
@ConditionalOnExpression("'${sps.data.store.strategy}'.equals('BATCH_STORE') and '${sps.data.store.adapter}'.equals('FILESYS_RDBMS')")
public class ScreenshotBatchStore_FileRDBMS implements ScreenshotStoreService {

    @Override
    public void storeScreenshot(
            final String sessionUUID,
            final Long timestamp,
            final ImageFormat imageFormat,
            final String metadata,
            final InputStream in) {
        // TODO Auto-generated method stub

    }

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    public void storeScreenshot(final String sessionUUID, final InputStream in) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getStoreHealthIndicator() {
        // TODO Auto-generated method stub
        return 0;
    }

}
