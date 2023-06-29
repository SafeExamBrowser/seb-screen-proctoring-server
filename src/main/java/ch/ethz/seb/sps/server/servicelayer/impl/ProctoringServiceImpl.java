/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;

import ch.ethz.seb.sps.domain.api.API.PrivilegeType;
import ch.ethz.seb.sps.domain.api.APIErrorException;
import ch.ethz.seb.sps.domain.api.JSONMapper;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.PageSortOrder;
import ch.ethz.seb.sps.domain.model.service.Group;
import ch.ethz.seb.sps.domain.model.service.MonitoringPageData;
import ch.ethz.seb.sps.domain.model.service.ScreenshotViewData;
import ch.ethz.seb.sps.domain.model.service.Session;
import ch.ethz.seb.sps.server.ServiceInfo;
import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataRecord;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDAO;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDataDAO;
import ch.ethz.seb.sps.server.servicelayer.ProctoringService;
import ch.ethz.seb.sps.server.servicelayer.UserService;
import ch.ethz.seb.sps.utils.Result;

@Lazy
@Component
public class ProctoringServiceImpl implements ProctoringService {

    private static final Logger log = LoggerFactory.getLogger(ProctoringServiceImpl.class);

    private final ScreenshotDAO screenshotDAO;
    private final ScreenshotDataDAO screenshotDataDAO;
    private final ProctoringCacheService proctoringCacheService;
    private final UserService userService;
    private final ServiceInfo serviceInfo;
    private final JSONMapper jsonMapper;

    public ProctoringServiceImpl(
            final ScreenshotDAO screenshotDAO,
            final ScreenshotDataDAO screenshotDataDAO,
            final ProctoringCacheService proctoringCacheService,
            final UserService userService,
            final ServiceInfo serviceInfo,
            final JSONMapper jsonMapper) {

        this.screenshotDAO = screenshotDAO;
        this.screenshotDataDAO = screenshotDataDAO;
        this.proctoringCacheService = proctoringCacheService;
        this.userService = userService;
        this.serviceInfo = serviceInfo;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void checkMonitroingAccess(final String groupUUID) {
        final Group activeGroup = this.proctoringCacheService.getActiveGroup(groupUUID);
        if (activeGroup == null) {
            throw APIErrorException.notFound(EntityType.SEB_GROUP, groupUUID, "Group doesn't exist or is not active");
        }

        this.userService.check(PrivilegeType.READ, activeGroup);
    }

    @Override
    public Result<ScreenshotViewData> getLiveImageData(final String sessionUUID) {
        return this.screenshotDataDAO.getLatest(sessionUUID)
                .map(data -> createScreenshotViewData(sessionUUID, data));
    }

    @Override
    public Result<ScreenshotViewData> getRecordedImageDataAt(final String sessionUUID, final Long timestamp) {
        return this.screenshotDataDAO.getAt(sessionUUID, timestamp)
                .map(data -> createScreenshotViewData(sessionUUID, data));
    }

    @Override
    public Result<MonitoringPageData> getMonitoringPageData(
            final String groupUUID,
            final Integer pageNumber,
            final Integer pageSize,
            final String sortBy,
            final PageSortOrder sortOrder) {

        return Result.tryCatch(() -> {

            final int pnum = (pageNumber != null) ? pageNumber - 1 : 0;
            final int pSize = (pageSize != null && pageSize < 20) ? pageSize : 9;

            final Collection<String> sessionTokens = this.proctoringCacheService
                    .getSessionTokens(groupUUID);

            final List<String> sessionIdsInOrder = sessionTokens
                    .stream()
                    .map(this.proctoringCacheService::getSession)
                    .sorted(Session.getComparator(sortBy, sortOrder == PageSortOrder.DESCENDING))
                    .skip(pnum * pSize)
                    .limit(pSize)
                    .map(Session::getUuid)
                    .collect(Collectors.toList());

            final Map<String, ScreenshotDataRecord> mapping = this.screenshotDataDAO
                    .allLatestIn(sessionIdsInOrder)
                    .getOrThrow();

            final List<ScreenshotViewData> page = sessionIdsInOrder.stream()
                    .map(sid -> createScreenshotViewData(sid, mapping.get(sid)))
                    .collect(Collectors.toList());

            return new MonitoringPageData(
                    groupUUID,
                    sessionTokens.size(),
                    pnum,
                    pSize,
                    sortBy,
                    sortOrder,
                    page);
        });
    }

    @Override
    public void streamScreenshot(final String screenshotId, final OutputStream out) {
        try {

            final InputStream screenshotIn = this.screenshotDAO
                    .getImage(screenshotId)
                    .getOrThrow();

            IOUtils.copy(screenshotIn, out);

        } catch (final Exception e) {
            log.error("Failed to get latest screenshot image: ", e);
        }
    }

    public void clearGroupCache(final String groupUUID, final boolean fully) {
        if (fully) {
            this.proctoringCacheService
                    .getSessionTokens(groupUUID)
                    .stream()
                    .forEach(this.proctoringCacheService::evictSession);
        }
        this.proctoringCacheService.evictGroup(groupUUID);
    }

    private ScreenshotViewData createScreenshotViewData(
            final String sessionUUID,
            final ScreenshotDataRecord data) {

        try {

            final Session session = this.proctoringCacheService.getSession(sessionUUID);
            final String imageLink = this.serviceInfo.getScreenshotRequestURI() + "/" + data.getId();
            final Map<String, String> metaData = this.jsonMapper.readValue(
                    data.getMetaData(),
                    new TypeReference<Map<String, String>>() {
                    });

            return new ScreenshotViewData(
                    data.getTimestamp(),
                    session,
                    imageLink,
                    metaData);

        } catch (final Exception e) {
            log.error("Failed to create ScreenshotViewData for session: {} and data: {}", sessionUUID, data, e);
            return null;
        }
    }

}
