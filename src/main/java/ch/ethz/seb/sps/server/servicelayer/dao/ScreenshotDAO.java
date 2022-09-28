/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.dao;

import java.io.InputStream;

import ch.ethz.seb.sps.utils.Result;

public interface ScreenshotDAO {

    Result<InputStream> getImage(
            Long pk,
            final String sessionId);

    Result<Long> storeImage(
            Long pk,
            String sessionId,
            InputStream in);

}
