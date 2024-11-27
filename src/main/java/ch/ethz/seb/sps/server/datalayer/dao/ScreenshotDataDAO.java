/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao;

import java.sql.Date;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import ch.ethz.seb.sps.domain.model.PageSortOrder;
import ch.ethz.seb.sps.domain.model.service.UserListForApplicationSearch;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.service.ScreenshotData;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataRecord;
import ch.ethz.seb.sps.utils.Constants;
import ch.ethz.seb.sps.utils.Result;

public interface ScreenshotDataDAO extends EntityDAO<ScreenshotData, ScreenshotData> {

    Result<Collection<ScreenshotData>> allOfSession(String sessionUUID);

    Result<ScreenshotDataRecord> getAt(String sessionUUID, Long at);

    Result<Long> getIdAt(String sessionUUID, Long at);

    Result<Long> getLatestImageId(String sessionUUID);

    Result<Collection<Long>> getScreenshotTimestamps(String sessionUUID, Long timestamp, PageSortOrder sortOrder);

    Result<ScreenshotDataRecord> getLatest(String sessionUUID);

    @Transactional(readOnly = true)
    default Result<Map<String, ScreenshotDataRecord>> allLatestOf(final String sessionUUIDList) {
        return Result.tryCatch(() -> {

            final List<String> ids = sessionUUIDList.contains(Constants.LIST_SEPARATOR)
                    ? Arrays.asList(StringUtils.split(sessionUUIDList, Constants.LIST_SEPARATOR))
                    : Arrays.asList(sessionUUIDList);

            return allLatestIn(ids).getOrThrow();
        });
    }

    Result<Map<String, ScreenshotDataRecord>> allLatestIn(List<String> sessionUUIDs);

    Result<Long> save(
            String sessionId,
            Long timestamp,
            ImageFormat imageFormat,
            String metadata);

    Result<Collection<ScreenshotDataRecord>> searchScreenshotData(FilterMap filterMap);

    Result<List<Date>> selectMatchingScreenshotDataPerDay(FilterMap filterMap);

    Result<Collection<String>> getDistinctMetadataAppForExam(List<Long> groupIds);

    Result<Collection<String>> getDistinctMetadataWindowForExam(String metadataApplication, List<Long> groupIds);

    Result<Long> countDistinctMetadataWindowForExam(String metadataApplication, List<Long> groupIds);

    Result<List<UserListForApplicationSearch>> getUserListForApplicationSearch(String metadataWindowTitle, String metadataApplication, List<Long> groupIds);

    Result<List<Long>> getTimestampListForApplicationSearch(String sessionUuid, String metadataApplication, String metadataWindowTitle);

}