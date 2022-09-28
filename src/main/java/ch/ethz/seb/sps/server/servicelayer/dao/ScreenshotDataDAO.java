/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.dao;

import java.util.Collection;

import ch.ethz.seb.sps.domain.model.screenshot.ScreenshotData;
import ch.ethz.seb.sps.utils.Result;

public interface ScreenshotDataDAO {

    Result<ScreenshotData> byPK(Long id);

    Result<Collection<ScreenshotData>> allOfSession(String sessionUUID);

    Result<Collection<ScreenshotData>> allLatestOfSessions(String sessionUUID);

    Result<Long> save(ScreenshotData data);

    Result<Long> getLatestScreenshotId(String sessionId);

}
