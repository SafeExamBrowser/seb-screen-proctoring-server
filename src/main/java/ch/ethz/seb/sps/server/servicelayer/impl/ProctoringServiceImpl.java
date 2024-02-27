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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import ch.ethz.seb.sps.domain.model.service.GroupViewData;
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
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.PageSortOrder;
import ch.ethz.seb.sps.domain.model.service.Exam;
import ch.ethz.seb.sps.domain.model.service.ExamViewData;
import ch.ethz.seb.sps.domain.model.service.Group;
import ch.ethz.seb.sps.domain.model.service.MonitoringPageData;
import ch.ethz.seb.sps.domain.model.service.ScreenshotSearchResult;
import ch.ethz.seb.sps.domain.model.service.ScreenshotViewData;
import ch.ethz.seb.sps.domain.model.service.Session;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import ch.ethz.seb.sps.domain.model.service.SessionSearchResult;
import ch.ethz.seb.sps.server.ServiceInfo;
import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataRecord;
import ch.ethz.seb.sps.server.datalayer.dao.ExamDAO;
import ch.ethz.seb.sps.server.datalayer.dao.GroupDAO;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDAO;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDataDAO;
import ch.ethz.seb.sps.server.datalayer.dao.SessionDAO;
import ch.ethz.seb.sps.server.servicelayer.ProctoringService;
import ch.ethz.seb.sps.server.servicelayer.UserService;
import ch.ethz.seb.sps.utils.Constants;
import ch.ethz.seb.sps.utils.Result;
import ch.ethz.seb.sps.utils.Utils;

@Lazy
@Component
public class ProctoringServiceImpl implements ProctoringService {

    private static final Logger log = LoggerFactory.getLogger(ProctoringServiceImpl.class);

    private final ExamDAO examDAO;
    private final GroupDAO groupDAO;
    private final SessionDAO sessionDAO;
    private final ScreenshotDAO screenshotDAO;
    private final ScreenshotDataDAO screenshotDataDAO;
    private final ProctoringCacheService proctoringCacheService;
    private final UserService userService;
    private final ServiceInfo serviceInfo;
    private final JSONMapper jsonMapper;

    public ProctoringServiceImpl(
            final ExamDAO examDAO,
            final GroupDAO groupDAO,
            final SessionDAO sessionDAO,
            final ScreenshotDAO screenshotDAO,
            final ScreenshotDataDAO screenshotDataDAO,
            final ProctoringCacheService proctoringCacheService,
            final UserService userService,
            final ServiceInfo serviceInfo,
            final JSONMapper jsonMapper) {

        this.examDAO = examDAO;
        this.groupDAO = groupDAO;
        this.sessionDAO = sessionDAO;
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
    public void checkMonitoringSessionAccess(final String sessionUUID) {
        final Session session = this.proctoringCacheService.getSession(sessionUUID);
        if (session == null) {
            throw APIErrorException.notFound(EntityType.SESSION, sessionUUID, "Session doesn't exist or is not active");
        }

        // TODO get group for session and check group access
    }

    @Override
    public Result<ScreenshotViewData> getRecordedImageDataAt(final String sessionUUID, final Long timestamp) {
        if (timestamp != null) {
            return this.screenshotDataDAO
                    .getAt(sessionUUID, timestamp)
                    .map(data -> createScreenshotViewData(sessionUUID, data, null));
        } else {
            return this.screenshotDataDAO
                    .getLatest(sessionUUID)
                    .map(data -> createScreenshotViewData(sessionUUID, data, null));
        }
    }

    @Override
    public Result<MonitoringPageData> getMonitoringPageData(
            final String groupUUID,
            final Integer pageNumber,
            final Integer pageSize,
            final String sortBy,
            final PageSortOrder sortOrder,
            final FilterMap filterMap) {

        return Result.tryCatch(() -> {

            if (serviceInfo.isDistributed()) {
                updateSessionCache(groupUUID);
            }

            final Group activeGroup = this.proctoringCacheService.getActiveGroup(groupUUID);
            final Collection<String> sessionTokens = this.proctoringCacheService
                    .getSessionTokens(activeGroup.uuid, activeGroup.id);

            final long millisecondsNow = Utils.getMillisecondsNow();
            final int pSize = (pageSize != null && pageSize < 20) ? pageSize : 9;
            final int pnum = (pageNumber == null || pageNumber < 1) ? 1
                    : ((pageNumber - 1) * pSize > sessionTokens.size()) ? 1 : pageNumber;

            final List<String> sessionIdsInOrder = sessionTokens
                    .stream()
                    .map(this.proctoringCacheService::getSession)
                    .sorted(Session.getComparator(sortBy, sortOrder == PageSortOrder.DESCENDING))
                    .skip((long) (pnum - 1) * pSize)
                    .limit(pSize)
                    .map(Session::getUuid)
                    .collect(Collectors.toList());

            final Map<String, ScreenshotDataRecord> mapping = this.screenshotDataDAO
                    .allLatestIn(sessionIdsInOrder)
                    .getOrThrow();

            final List<ScreenshotViewData> page = sessionIdsInOrder.stream()
                    .map(sid -> createScreenshotViewData(sid, mapping.get(sid), millisecondsNow))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            ExamViewData examViewData = ExamViewData.EMPTY_MODEL;
            if (activeGroup.getExam_id() != null) {
                final Exam exam = this.examDAO.byModelId(activeGroup.exam_id.toString()).getOr(null);
                examViewData = new ExamViewData(exam.uuid, exam.name);
            }

            return new MonitoringPageData(
                    groupUUID,
                    activeGroup.name,
                    activeGroup.description,
                    sessionTokens.size(),
                    pnum,
                    pSize,
                    sortBy,
                    sortOrder,
                    page,
                    examViewData);
        });
    }



    @Override
    public void streamScreenshot(
            final String sessionUUID,
            final Long timestamp,
            final Consumer<String> mimeTypePropagation,
            final OutputStream out) {

        final Session session = this.proctoringCacheService.getSession(sessionUUID);
        if (session != null) {

            mimeTypePropagation.accept(session.imageFormat.mimeType);

            if (timestamp != null) {
                streamScreenshotAt(sessionUUID, timestamp, out);
            } else {
                streamLatestScreenshot(sessionUUID, out);
            }

        } else {
            throw new IllegalStateException("No active session found for id: " + sessionUUID);
        }
    }

    @Override
    public void streamScreenshot(
            final Long screenshotId,
            final String sessionUUID,
            final OutputStream out) {

        try {

            final InputStream screenshotIn = this.screenshotDAO
                    .getImage(screenshotId, sessionUUID)
                    .getOrThrow();

            IOUtils.copy(screenshotIn, out);

        } catch (final Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Result<Collection<SessionSearchResult>> searchSessions(final FilterMap filterMap) {
        return this.sessionDAO
                .allMatching(filterMap)
                .map(data -> this.createSessionSearchResult(data, filterMap));
    }

    @Override
    public Result<Collection<ScreenshotSearchResult>> searchScreenshots(final FilterMap filterMap) {
        return this.screenshotDataDAO
                .searchScreenshotData(filterMap)
                .map(this::createScreenshotSearchResult);
    }

    @Override
    public Collection<ScreenshotSearchResult> createScreenshotSearchResult(
            final Collection<ScreenshotDataRecord> data) {

        final Map<String, Session> sessionCache = new HashMap<>();
        final Map<Long, Group> groupCache = new HashMap<>();
        return data
                .stream()
                .map(rec -> toScreenshotSearchResult(rec, groupCache, sessionCache))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public ScreenshotSearchResult toScreenshotSearchResult(
            final ScreenshotDataRecord rec,
            final Map<Long, Group> groupCache,
            final Map<String, Session> sessionCache) {

        try {

            // check cache and fill if needed
            sessionCache.computeIfAbsent(rec.getSessionUuid(), uuid -> {
                final Session session = this.sessionDAO.byModelId(uuid).getOr(null);
                groupCache.computeIfAbsent(
                        session.getGroupId(),
                        gid -> this.groupDAO.byPK(gid).getOr(null));
                return session;
            });

            final Session session = sessionCache.get(rec.getSessionUuid());
            final Group group = groupCache.get(session.getGroupId());

            ImageFormat imageFormat = session.getImageFormat();
            if (rec.getImageFormat() != null) {
                try {
                    imageFormat = ImageFormat.valueOf(rec.getImageFormat());
                } catch (final Exception e) {
                    log.error("Failed to get imageFormat from screenshot: ", e);
                }
            }

            return new ScreenshotSearchResult(
                    rec.getId(),
                    group,
                    session,
                    rec.getTimestamp(),
                    imageFormat,
                    this.extractedMetaData(rec));

        } catch (final Exception e) {
            log.error("Failed to convert ScreenshotDataRecord to ScreenshotViewData for {}", rec, e);
            return null;
        }
    }

    @Override
    public Map<String, String> extractedMetaData(final ScreenshotDataRecord data) {

        final Map<String, String> metaData = new HashMap<>();

        try {

            metaData.putAll(
                    this.jsonMapper.readValue(
                            data.getMetaData(),
                            new TypeReference<Map<String, String>>() {
                            }));

        } catch (final Exception e) {
            log.warn("Failed to parse meta data JSON, add it as single attribute: {}", data.getMetaData());
            metaData.put("data", data.getMetaData());
        }

        return metaData;
    }

    private void streamLatestScreenshot(final String sessionUUID, final OutputStream out) {
        try {

            final InputStream screenshotIn = this.screenshotDataDAO
                    .getLatestImageId(sessionUUID)
                    .flatMap(pk -> this.screenshotDAO.getImage(pk, sessionUUID))
                    .getOrThrow();

            IOUtils.copy(screenshotIn, out);

        } catch (final Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    private void streamScreenshotAt(
            final String sessionUUID,
            final Long timestamp,
            final OutputStream out) {
        try {

            final InputStream screenshotIn = this.screenshotDataDAO
                    .getIdAt(sessionUUID, timestamp)
                    .flatMap(pk -> this.screenshotDAO.getImage(pk, sessionUUID))
                    .getOrThrow();

            IOUtils.copy(screenshotIn, out);

        } catch (final Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public void clearGroupCache(final String groupUUID, final boolean fully) {
        if (fully) {
            final Group activeGroup = this.proctoringCacheService.getActiveGroup(groupUUID);
            this.proctoringCacheService
                    .getSessionTokens(groupUUID, activeGroup.id)
                    .stream()
                    .forEach(this.proctoringCacheService::evictSession);
        }
        this.proctoringCacheService.evictGroup(groupUUID);
    }

    private Collection<SessionSearchResult> createSessionSearchResult(
            final Collection<Session> data,
            final FilterMap filterMap) {

        final Map<Long, GroupViewData> groupCache = new HashMap<>();
        return data
                .stream()
                .map(session -> toSessionSearchResult(session, groupCache, filterMap))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private SessionSearchResult toSessionSearchResult(
            final Session session,
            final Map<Long, GroupViewData> groupCache,
            final FilterMap filterMap) {

        groupCache.computeIfAbsent(
                session.getGroupId(),
                gid -> this.groupDAO.getGroupWithExamData(gid).getOr(null));

        final GroupViewData group = groupCache.get(session.getGroupId());
        final Long nrOfScreenshots = this.sessionDAO.getNumberOfScreenshots(
                session.uuid,
                filterMap);

        if (nrOfScreenshots == null || nrOfScreenshots <= 0) {
            return null;
        }

        return new SessionSearchResult(
                session,
                group,
                nrOfScreenshots.intValue()
        );
    }

    private ScreenshotViewData createScreenshotViewData(
            final String sessionUUID,
            final ScreenshotDataRecord data,
            final Long timestamp) {

        if (data == null) {
            return null;
        }

        try {

            final Session session = this.proctoringCacheService.getSession(sessionUUID);
            return createViewData(data, session);

        } catch (final Exception e) {
            log.error("Failed to create ScreenshotViewData for session: {} and data: {}", sessionUUID, data, e);
            return null;
        }
    }

    private ScreenshotViewData createViewData(
            final ScreenshotDataRecord data,
            final Session session) {

        final String imageLink = this.serviceInfo.getScreenshotRequestURI() + "/" + data.getSessionUuid();
        final Map<String, String> metaData = extractedMetaData(data);

        return new ScreenshotViewData(
                session.creationTime,
                data.getTimestamp(),
                session.terminationTime != null ? session.terminationTime : data.getTimestamp(),
                session.uuid,
                session.clientName,
                session.clientIP,
                session.clientMachineName,
                session.clientOSName,
                session.clientVersion,
                session.imageFormat,
                imageLink,
                imageLink + Constants.SLASH + data.getTimestamp(),
                metaData);
    }

    private long lastUpdateTime = 0;
    private void updateSessionCache(String groupUUID) {
        long now = Utils.getMillisecondsNow();
        if (now - lastUpdateTime > this.serviceInfo.getDistributedUpdateInterval()) {
            // TODO instead of evict the while cache, check updates on session and make it per outdated session
            this.clearGroupCache(groupUUID, true);
            lastUpdateTime = now;
        }
    }

}
