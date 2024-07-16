/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer;

import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.PageSortOrder;
import ch.ethz.seb.sps.domain.model.service.*;
import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataRecord;
import ch.ethz.seb.sps.utils.Result;

import java.io.OutputStream;
import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface ProctoringService {

    /** Check current users monitoring access for given groupUUID
     *
     * @param groupUUID the groups UUID */
    void checkMonitoringAccess(String groupUUID);

    /** Check current users monitoring access for given sessionUUID
     *
     * @param sessionUUID the sessions UUID */
    void checkMonitoringSessionAccess(String sessionUUID);

    /** Get the recorded image data and metadata at a given point of time.
     * <p>
     * If the timestamp is null this returns the data for the last available screenshot
     * <p>
     * If there is no screenshot at the exact given timestamp this returns the screenshot on
     * the next previous point of time and if there is no such previous screenshot, this
     * returns the screenshot on the next point in time from the given.
     *
     * @param sessionUUID The session identifier
     * @param timestamp the point in time (UTC/milliseconds)
     * @return Result refer to the screenshot data or to an error when happened */
    Result<ScreenshotViewData> getRecordedImageDataAt(String sessionUUID, Long timestamp);

    Result<ScreenshotsInGroupData> getMonitoringPageData(
            String groupUUID,
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            PageSortOrder sortOrder,
            FilterMap filterMap);

    Collection<ScreenshotSearchResult> createScreenshotSearchResult(Collection<ScreenshotDataRecord> data);

    ScreenshotSearchResult toScreenshotSearchResult(
            ScreenshotDataRecord rec,
            Map<Long, Group> groupCache,
            Map<String, Session> sessionCache);

    Map<String, String> extractedMetaData(ScreenshotDataRecord data);

    /** Gets through all active groups and counts active and total session for these groups.
     * @return Result refer to collection of all group session counts or to an error when happened */
    Result<Collection<GroupSessionCount>> getActivateGroupSessionCounts();

    void streamScreenshot(
            String sessionUUID,
            Long timestamp,
            Consumer<String> mimeTypePropagation,
            OutputStream out);

    void streamScreenshot(
            Long screenhotId,
            String sessionUUID,
            OutputStream out);

    /** This executes the session search and returns a list for the requested filter criteria
     *
     * @param filterMap Contains all filter criteria
     * @return Result refer to the requested list of search results or to an error when happened */

    Result<Collection<SessionSearchResult>> searchSessions(FilterMap filterMap);

    Result<Collection<ScreenshotSearchResult>> searchScreenshots(final FilterMap filterMap);

    Result<List<Date>> queryMatchingDaysForSessionSearch(FilterMap filterMap);

    Result<Exam> updateCacheForExam(Exam exam);
}