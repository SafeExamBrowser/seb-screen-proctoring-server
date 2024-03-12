/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao;

import java.io.InputStream;
import java.util.List;

import ch.ethz.seb.sps.utils.Result;

public interface ScreenshotDAO {

    Result<InputStream> getImage(
            Long pk,
            String sessionUUID);

    Result<Long> storeImage(
            Long pk,
            String sessionUUID,
            InputStream in);

    Result<List<Long>> deleteAllForSession(
            String sessionUUID,
            List<Long> screenShotPKs);

}
