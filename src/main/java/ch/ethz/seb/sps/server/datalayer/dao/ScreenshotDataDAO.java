/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import ch.ethz.seb.sps.domain.model.service.ScreenshotData;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import ch.ethz.seb.sps.utils.Result;

public interface ScreenshotDataDAO extends EntityDAO<ScreenshotData, ScreenshotData> {

    Result<Collection<ScreenshotData>> allOfSession(String sessionUUID);

    Result<Map<String, ScreenshotData>> allLatestOfSessions(String sessionUUID);

    Result<Map<String, ScreenshotData>> allLatestIn(List<String> sessionUUIDs);

    Result<Long> save(
            String sessionId,
            Long timestamp,
            ImageFormat imageFormat,
            String metadata);

    Result<Long> getLatestScreenshotId(String sessionId);

}
