/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import javax.cache.Cache;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import ch.ethz.seb.sps.domain.model.service.*;
import ch.ethz.seb.sps.domain.model.service.DistinctMetadataWindowForExam;
import ch.ethz.seb.sps.server.datalayer.dao.*;
import ch.ethz.seb.sps.server.servicelayer.LiveProctoringCacheService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.ehcache.core.Ehcache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.jcache.JCacheCache;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;

import ch.ethz.seb.sps.domain.api.API.PrivilegeType;
import ch.ethz.seb.sps.domain.api.APIErrorException;
import ch.ethz.seb.sps.domain.api.JSONMapper;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.PageSortOrder;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import ch.ethz.seb.sps.server.ServiceInfo;
import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataRecord;
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
    private final LiveProctoringCacheService liveProctoringCacheService;
    private final UserService userService;
    private final ServiceInfo serviceInfo;
    private final JSONMapper jsonMapper;
    private final CacheManager cacheManager;
    private final boolean isDistributedSetup;
    private final long distributedUpdateInterval;

    public ProctoringServiceImpl(
            final ExamDAO examDAO,
            final GroupDAO groupDAO,
            final SessionDAO sessionDAO,
            final ScreenshotDAO screenshotDAO,
            final ScreenshotDataDAO screenshotDataDAO,
            final ProctoringCacheService proctoringCacheService,
            final LiveProctoringCacheService liveProctoringCacheService,
            final UserService userService,
            final ServiceInfo serviceInfo,
            final JSONMapper jsonMapper, 
            final CacheManager cacheManager) {

        this.examDAO = examDAO;
        this.groupDAO = groupDAO;
        this.sessionDAO = sessionDAO;
        this.screenshotDAO = screenshotDAO;
        this.screenshotDataDAO = screenshotDataDAO;
        this.proctoringCacheService = proctoringCacheService;
        this.liveProctoringCacheService = liveProctoringCacheService;
        this.userService = userService;
        this.serviceInfo = serviceInfo;
        this.jsonMapper = jsonMapper;
        this.distributedUpdateInterval = serviceInfo.getDistributedUpdateInterval();
        this.isDistributedSetup = serviceInfo.isDistributed();
        this.cacheManager = cacheManager;
    }

    @Override
    public void checkMonitoringAccess(final String groupUUID) {
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
    public synchronized Result<ScreenshotViewData> getRecordedImageDataAt(final String sessionUUID, final Long timestamp) {
        return Result.tryCatch(() -> {
            final SessionScreenshotCacheData sessionScreenshotData = this.proctoringCacheService
                    .getSessionScreenshotData(sessionUUID);
            return createScreenshotViewData(
                    sessionUUID,
                    sessionScreenshotData.getAt(timestamp));
        });
    }

    @Override
    public synchronized Result<ScreenshotsInGroupData> getSessionsByGroup(
            final String groupUUID,
            final Integer pageNumber,
            final Integer pageSize,
            final String sortBy,
            final PageSortOrder sortOrder,
            final FilterMap filterMap) {

        return Result.tryCatch(() -> {
            
            updateSessionCache(groupUUID);

            final Group activeGroup = this.proctoringCacheService.getActiveGroup(groupUUID);
            final Collection<String> liveSessionTokens = this.proctoringCacheService
                    .getLiveSessionTokens(activeGroup.uuid);

            //SEBSP-145 modify "getLiveSessionTokens" to determine inactivity via screenshotData Table (join will likely cause performance issues)
            
            final int liveSessionCount = liveSessionTokens.size();
            Integer totalSessionCount = this.proctoringCacheService.getTotalSessionCount(activeGroup.uuid, activeGroup.id);
            final int sessionCount = totalSessionCount != null ? totalSessionCount : -1;

            //SEBSP-145 - add count for inactive sessions

            final long millisecondsNow = Utils.getMillisecondsNow();
            final int pSize = (pageSize != null && pageSize < 20) ? pageSize : 9;
            final int pnum = (pageNumber == null || pageNumber < 1) ? 1
                    : ((pageNumber - 1) * pSize > liveSessionCount) ? 1 : pageNumber;

            final List<String> sessionIdsInOrder = liveSessionTokens
                    .stream()
                    .map(this.proctoringCacheService::getSession)
                    .sorted(
                            sortOrder == PageSortOrder.ASCENDING ?
                            Comparator.comparing(Session::getClientName) :
                            Comparator.comparing(Session::getClientName).reversed()
                    ) // sort by client name
                    .skip((long) (pnum - 1) * pSize)
                    .limit(pSize)
                    .map(Session::getUuid)
                    .toList();
            
            final Map<String, ScreenshotDataRecord> mapping = this.screenshotDataDAO
                    .allOfMappedToSession(
                            sessionIdsInOrder
                                    .stream()
                                    .map(uuid -> liveProctoringCacheService.getLatestSSDataId(uuid, true))
                                    .toList())
                    .getOrThrow();

            final List<ScreenshotViewData> page = sessionIdsInOrder.stream()
                    .map(sid -> createScreenshotViewData(sid, mapping.get(sid)))
                    //SEBSP-145 - remove inactive sessions from page / add flag to indicative inactivity
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            ExamViewData examViewData = ExamViewData.EMPTY_MODEL;
            if (activeGroup.getExam_id() != null) {
                // TODO since Exam data is also needed on monitoring now and requested every update
                //      we also need a cache for Exam to not make a DB request on every update
                final Exam exam = this.examDAO.byModelId(activeGroup.exam_id.toString()).getOr(null);
                examViewData = new ExamViewData(exam.uuid, exam.name, exam.startTime, exam.endTime);
            }

            return new ScreenshotsInGroupData(
                    groupUUID,
                    activeGroup.name,
                    activeGroup.description,
                    liveSessionCount,
                    sessionCount,
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
            IOUtils.closeQuietly(screenshotIn);
        } catch (final Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        } 
    }

    @Override
    public Result<List<Date>> queryMatchingDaysForSessionSearch(final FilterMap filterMap) {
        return this.sessionDAO
                .queryMatchingDaysForSessionSearch(filterMap)
                .map(data -> this.createSessionDaySearchResult(data, filterMap));
    }

    @Override
    public Result<Exam> updateCacheForExam(Exam exam) {
        return Result.tryCatch(() -> {
            this.groupDAO
                    .allIdsForExamsIds(Collections.singletonList(exam.id))
                    .getOrThrow()
                    .stream()
                    .map(gid -> groupDAO.byPK(gid).getOr(null))
                    .filter(Objects::nonNull)
                    .forEach(group -> this.clearGroupCache(group.uuid, false));

            return exam;
        });
    }

    @Override
    public Result<Collection<ScreenshotSearchResult>> searchScreenshots(final FilterMap filterMap) {
        return this.screenshotDataDAO
                .searchScreenshotData(filterMap)
                .map(this::createScreenshotSearchResult);
    }

    @Override
    public Result<Collection<SessionSearchResult>> searchSessions(final FilterMap filterMap) {
        return this.sessionDAO
                .allMatching(filterMap)
                .map(data -> this.createSessionSearchResult(data, filterMap));
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

        if (data.getMetaData() == null) {
            return metaData;
        }

        try {

            metaData.putAll(
                    this.jsonMapper.readValue(
                            data.getMetaData(),
                            new TypeReference<Map<String, String>>() {
                            }));

        } catch (final Exception e) {
            log.warn(
                    "Failed to parse meta data JSON, add it as single attribute: {} error: {}",
                    data.getMetaData(),
                    e.getMessage());
            metaData.put("data", data.getMetaData());
        }

        return metaData;
    }

    @Override
    public Result<Collection<GroupSessionCount>> getActivateGroupSessionCounts() {
        return Result.tryCatch(() -> {
            this.userService.check(PrivilegeType.READ, EntityType.SEB_GROUP);
            
            return this.groupDAO
                    .activeGroupUUIDs()
                    .getOrThrow()
                    .stream()
                    .peek(this::updateSessionCache)
                    .map(this.proctoringCacheService::getActiveGroup)
                    .filter(Objects::nonNull)
                    .map(group -> new GroupSessionCount(
                            group.uuid,
                            this.proctoringCacheService.getLiveSessionTokens(group.uuid).size(),
                            this.proctoringCacheService.getTotalSessionCount(group.uuid, group.id)))
                    .collect(Collectors.toSet());
        });
    }

    @Override
    public void clearGroupCache(final String groupUUID, final boolean fully) {

        if (log.isDebugEnabled()) {
            log.debug("Clear group cache request for group: {}, full cache flush: {}", groupUUID, fully);
        }

        if (fully) {
            sessionDAO
                    .allSessionUUIDsByGroupId(groupDAO.modelIdToPK(groupUUID))
                    .onSuccess( all -> all.forEach(this.proctoringCacheService::evictSession));
        }
        this.proctoringCacheService.evictGroup(groupUUID);
    }

    @Override
    public DistinctMetadataWindowForExam getDistinctMetadataWindowForExam(final String metadataApplication, final List<Long> groupIds){
        return new DistinctMetadataWindowForExam(
                this.screenshotDataDAO.countDistinctMetadataWindowForExam(metadataApplication, groupIds).getOrThrow(),
                this.screenshotDataDAO.getDistinctMetadataWindowForExam(metadataApplication, groupIds).getOrThrow()
        );
    }

    private void streamLatestScreenshot(final String sessionUUID, final OutputStream out) {
        InputStream screenshotIn = null;
        try {
            
            screenshotIn =  this.screenshotDAO
                    .getImage(this.liveProctoringCacheService.getLatestSSDataId(sessionUUID, true), sessionUUID)
                    .getOrThrow();

            IOUtils.copy(screenshotIn, out);
            
        } catch (final Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        } finally {
            IOUtils.closeQuietly(screenshotIn);
        }
    }

    private void streamScreenshotAt(
            final String sessionUUID,
            final Long timestamp,
            final OutputStream out) {
        
        if (timestamp == null) {
            streamLatestScreenshot(sessionUUID, out);
            return;
        }

        InputStream screenshotIn = null;
        try {

            final ScreenshotDataRecord at = this.proctoringCacheService
                    .getSessionScreenshotData(sessionUUID)
                    .getAt(timestamp);

            screenshotIn = this.screenshotDAO
                    .getImage(at.getId(), sessionUUID)
                    .getOrThrow();

            IOUtils.copy(screenshotIn, out);
        } catch (final Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        } finally {
            IOUtils.closeQuietly(screenshotIn);
        }
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

    private List<Date> createSessionDaySearchResult(
            final List<Date> dateList,
            final FilterMap filterMap){

        if(!Utils.hasMetaDataCriteria(filterMap)){
            return dateList;
        }

        List<Date> screenshotDataSearchResult = this.screenshotDataDAO
                .selectMatchingScreenshotDataPerDay(filterMap)
                .getOrThrow();

        List<Date> finalSearchResult = compareSessionSearchWithScreenshotDataSearch(dateList, screenshotDataSearchResult);
        Collections.reverse(finalSearchResult);

        return finalSearchResult;
    }

    private List<Date> compareSessionSearchWithScreenshotDataSearch(final List<Date> sessionSearchResult, final List<Date> screenshotDataSearchResult){
        HashSet<Date> sessionResultCopy = new HashSet<>(sessionSearchResult);
        List<Date> commonDates = new ArrayList<>();

        for(Date date : screenshotDataSearchResult){
            if(sessionResultCopy.contains(date)){
                commonDates.add(date);
            }
        }

        return commonDates;
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

    private long lastUpdateTimeScreenshotViewData = 0;
    private ScreenshotViewData createScreenshotViewData(final String sessionUUID, final ScreenshotDataRecord data) {

        if (data == null) {
            return null;
        }

        try {
            
            // Note: for distributed setup we need to refresh the cache from time to time 
            if (this.isDistributedSetup) {
                long now = Utils.getMillisecondsNow();
                if (now - lastUpdateTimeScreenshotViewData > this.distributedUpdateInterval) {
                    this.proctoringCacheService.evictSession(sessionUUID);
                    lastUpdateTimeScreenshotViewData = now;
                }
            }
            
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
    private final Set<String> GROUP_UUID_UPDATE_REG = ConcurrentHashMap.newKeySet();
    private void updateSessionCache(String groupUUID) {
        if (!this.isDistributedSetup) {
            return;
        }

        long now = Utils.getMillisecondsNow();
        if (now - lastUpdateTime > this.distributedUpdateInterval) {
            GROUP_UUID_UPDATE_REG.clear();
            lastUpdateTime = now;
        }

        if (!GROUP_UUID_UPDATE_REG.contains(groupUUID)) {
            Group activeGroup = this.proctoringCacheService.getActiveGroup(groupUUID);
            if (activeGroup == null) {
                return;
            }

            if (this.groupDAO.needsUpdate(groupUUID, activeGroup.lastUpdateTime)) {
                this.clearGroupCache(groupUUID, true);
            } else {
                
                Map<String, Long> updateTimes = this.proctoringCacheService
                        .getLiveSessionTokens(activeGroup.uuid)
                        .stream()
                        .map(this.proctoringCacheService::getSession)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap( s -> s.uuid, s -> s.lastUpdateTime));
                
                if (!updateTimes.isEmpty()) {
                    this.sessionDAO
                            .allTokensThatNeedsUpdate(activeGroup.id, updateTimes)
                            .getOr(Collections.emptyList())
                            .forEach(this.proctoringCacheService::evictSession);
                }
            }
            proctoringCacheService.evictSessionTokens(groupUUID);
            GROUP_UUID_UPDATE_REG.add(groupUUID);
        }
    }

}
