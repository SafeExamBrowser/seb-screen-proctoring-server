/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer;

import java.io.OutputStream;
import java.util.Collection;
import java.util.function.Consumer;

import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.PageSortOrder;
import ch.ethz.seb.sps.domain.model.service.MonitoringPageData;
import ch.ethz.seb.sps.domain.model.service.ScreenshotSearchResult;
import ch.ethz.seb.sps.domain.model.service.ScreenshotViewData;
import ch.ethz.seb.sps.domain.model.service.SessionSearchResult;
import ch.ethz.seb.sps.utils.Result;

public interface ProctoringService {

    /** Check current users monitoring access for given groupUUID
     *
     * @param groupUUID */
    void checkMonitroingAccess(String groupUUID);

    /** Check current users monitoring access for given sessionUUID
     *
     * @param sessionUUID */
    void checkMonitroingSessionAccess(String sessionUUID);

    Result<ScreenshotViewData> getRecordedImageDataAt(String sessionUUID, Long timestamp);

    Result<MonitoringPageData> getMonitoringPageData(
            String groupUUID,
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            PageSortOrder sortOrder,
            FilterMap filterMap);

    void streamScreenshot(
            String sessionUUID,
            Long timestamp,
            Consumer<String> mimeTypePropagation,
            OutputStream out);

    void streamScreenshot(
            Long screenhotId,
            String sessionUUID,
            OutputStream out);

    Result<Collection<SessionSearchResult>> searchSessions(FilterMap filterMap);

    Result<Collection<ScreenshotSearchResult>> searchScreenshots(final FilterMap filterMap);

}
