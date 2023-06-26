/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer;

import java.io.OutputStream;

import org.springframework.scheduling.annotation.Async;

import ch.ethz.seb.sps.domain.model.PageSortOrder;
import ch.ethz.seb.sps.domain.model.service.MonitoringPageData;
import ch.ethz.seb.sps.domain.model.service.SessionData;
import ch.ethz.seb.sps.server.ServiceConfig;
import ch.ethz.seb.sps.utils.Result;

public interface ProctoringService {

    /** Check current users monitoring access for given group id
     *
     * @param groupUUID */
    void checkMonitroingAccess(String groupUUID);

    Result<SessionData> getSessionData(String sessionUUID, Long timestamp);

    Result<MonitoringPageData> getMonitoringPageData(
            String groupUUID,
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            PageSortOrder sortOrder);

    @Async(value = ServiceConfig.SCREENSHOT_DOWNLOAD_API_EXECUTOR)
    void streamScreenshot(String screenshotId, OutputStream out);

}
