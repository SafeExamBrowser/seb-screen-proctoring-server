/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import static ch.ethz.seb.sps.server.datalayer.dao.AdditionalAttributesDAO.ATTRIBUTE_SESSION_ALSO_CLOSE;

import java.util.*;

import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.server.datalayer.dao.AdditionalAttributesDAO;
import ch.ethz.seb.sps.server.datalayer.dao.ExamDAO;
import ch.ethz.seb.sps.server.servicelayer.ProctoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.APIErrorException;
import ch.ethz.seb.sps.domain.model.service.Group;
import ch.ethz.seb.sps.domain.model.service.Session;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import ch.ethz.seb.sps.server.datalayer.dao.GroupDAO;
import ch.ethz.seb.sps.server.datalayer.dao.SessionDAO;
import ch.ethz.seb.sps.server.servicelayer.SessionOnClosingEvent;
import ch.ethz.seb.sps.server.servicelayer.SessionService;
import ch.ethz.seb.sps.utils.Result;

@Service
public class SessionServiceImpl implements SessionService {

    private static final Logger log = LoggerFactory.getLogger(SessionServiceImpl.class);

    private final ExamDAO examDAO;
    private final GroupDAO groupDAO;
    private final SessionDAO sessionDAO;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ProctoringCacheService proctoringCacheService;
    private final ProctoringService proctoringService;
    private final AdditionalAttributesDAO additionalAttributesDAO;

    public SessionServiceImpl(
            final ExamDAO examDAO,
            final GroupDAO groupDAO,
            final SessionDAO sessionDAO,
            final ApplicationEventPublisher applicationEventPublisher,
            final ProctoringCacheService proctoringCacheService,
            final ProctoringService proctoringService, 
            final AdditionalAttributesDAO additionalAttributesDAO) {

        this.examDAO = examDAO;
        this.groupDAO = groupDAO;
        this.sessionDAO = sessionDAO;
        this.applicationEventPublisher = applicationEventPublisher;
        this.proctoringCacheService = proctoringCacheService;
        this.proctoringService = proctoringService;
        this.additionalAttributesDAO = additionalAttributesDAO;
    }

    @Override
    public Result<Session> createNewSession(
            final String groupUUID,
            final String userSessionName,
            final String clientIP,
            final String clientMachineName,
            final String clientOSName,
            final String clientVersion,
            final ImageFormat imageFormat) {

        return Result.tryCatch(() -> {
            if (groupUUID == null) {
                throw APIErrorException.ofMissingAttribute(Domain.SEB_GROUP.ATTR_UUID, "createNewSession");
            }

            final String newSessionId = UUID.randomUUID().toString();

            // check if group exists
            if (!this.groupDAO.existsByUUID(groupUUID)) {
                this.groupDAO
                        .createNew(groupUUID)
                        .getOrThrow();
            }

            // check group still active
            final Group activeGroup = this.proctoringCacheService.getActiveGroup(groupUUID);
            if (activeGroup == null) {
                throw APIErrorException.ofIllegalState(
                        "updateSessionData",
                        "Group is already closed",
                        groupUUID);
            }

            final Session session = this.sessionDAO
                    .createNew(
                            groupUUID,
                            newSessionId,
                            userSessionName,
                            clientIP,
                            clientMachineName,
                            clientOSName,
                            clientVersion,
                            imageFormat)
                    .getOrThrow();

            // caching update
            this.proctoringCacheService.evictSessionTokens(groupUUID);
            return session;
        });
    }

    @Override
    public Result<Session> updateSessionData(
            final String sessionUUID,
            final String userSessionName,
            final String clientIP,
            final String clientMachineName,
            final String clientOSName,
            final String clientVersion) {

        return this.sessionDAO
                .byModelId(sessionUUID)
                .map(this::checkUpdateIntegrity)
                .flatMap(session -> saveSession(
                        sessionUUID,
                        userSessionName,
                        clientIP,
                        clientMachineName,
                        clientOSName,
                        clientVersion,
                        session));
    }

    @Override
    public void closeSession(final String sessionUUID) {
        this.sessionDAO
                .byModelId(sessionUUID)
                .onSuccess(session -> {
                    try {
                        this.applicationEventPublisher.publishEvent(new SessionOnClosingEvent(sessionUUID));
                        final Result<String> result = this.sessionDAO.closeSession(sessionUUID);
                        if (!result.hasError()) {
                            // caching update
                            final String groupUUID = this.groupDAO.byPK(session.groupId).getOrThrow().uuid;
                            this.proctoringCacheService.evictSession(sessionUUID);
                            this.proctoringCacheService.evictSessionTokens(groupUUID);
                        } else {
                            result.getOrThrow();
                        }
                    } catch (Exception e) {
                        log.error("Failed to close session for sessionUUID: {} cause {}", sessionUUID, e.getMessage());
                    }
                });
    }

    @Override
    public Result<Collection<EntityKey>> closeAllSessions(final Collection<EntityKey> groupKeys) {
        return Result.tryCatch(() -> {
            Collection<EntityKey> result = new ArrayList<>(groupKeys);
            groupKeys.forEach(groupKey -> {

                this.sessionDAO
                        .closeAllSessionsForGroup(this.groupDAO.modelIdToPK(groupKey.modelId))
                        .onError(error -> log.warn(
                                "Failed to close SEB sessions for group: {} error: {}",
                                groupKey,
                                error.getMessage()))
                        // clear group cache
                        .onSuccess( results -> groupDAO
                                .byModelId(groupKey.modelId)
                                .onSuccess( gr -> proctoringService.clearGroupCache(gr.uuid, true))
                        )
                        // add results
                        .onSuccess(result::addAll);
            });
            return result;
        });
    }

    @Override
    public boolean hasAnySessionDataForExam(final String examUUID) {
        Long pk = examDAO.modelIdToPK(examUUID);
        System.out.println("*********************** requestDelete pk: " + pk);
        if (pk != null) {
            return this.groupDAO
                    .allIdsForExamsIds(List.of(pk))
                    .flatMap(sessionDAO::hasAnySessionData)
                    .onError(error -> log.warn("Failed to check if there are any session data for Exam: {} error: {}", examUUID, error.getMessage()))
                    .getOr(true);
        } else {
            log.warn("Failed to check if exam has sessions because of exam not found for uuid: {}", examUUID);
            return false;
        }
    }

    @Override
    public boolean hasAnySessionDataForGroup(final String groupUUID) {
        return sessionDAO
                .hasAnySessionData(Collections.singletonList(this.groupDAO.modelIdToPK(groupUUID)))
                .onError(error -> log.warn("Failed to check if there are any session data for Group: {} error: {}", groupUUID, error.getMessage()))
                .getOr(true);
    }

    @Override
    public boolean isSessionActive(final String sessionUUID) {
        try {
            return this.proctoringCacheService.getSession(sessionUUID).isActive();
        } catch (Exception e) {
            log.error("Failed to check if session is active: {} error: {}", sessionUUID, e.getMessage());
            return true;
        }
    }

    @Override
    public Result<String> markSessionForUpload(final String sessionUUID, final String uploadSessionUUID) {
        return this.sessionDAO.getEncryptionKey(uploadSessionUUID)
                .map(key -> {
                    
                    // open the  session for upload
                    this.sessionDAO
                            .setActive(new EntityKey(uploadSessionUUID, EntityType.SESSION), true)
                            .getOrThrow();
                    
                    // mark to close upload session too, when closing the current session
                    additionalAttributesDAO
                            .saveAdditionalAttribute(
                                    EntityType.SESSION, 
                                    sessionDAO.modelIdToPK(sessionUUID),
                                    ATTRIBUTE_SESSION_ALSO_CLOSE + "_" + uploadSessionUUID, 
                                    uploadSessionUUID)
                            .getOrThrow();
                    
                    return key;
                });
    }

    private Session checkUpdateIntegrity(final Session session) {
        if (session.terminationTime != null) {
            throw APIErrorException.ofIllegalState(
                    "updateSessionData",
                    "Session is already closed",
                    session);
        }
        return session;
    }

    private Result<Session> saveSession(
            final String sessionUUID,
            final String userSessionName,
            final String clientIP,
            final String clientMachineName,
            final String clientOSName,
            final String clientVersion,
            final Session session) {

        final Result<Session> result = this.sessionDAO.save(new Session(
                session.id,
                null, null,
                userSessionName,
                clientIP,
                clientMachineName,
                clientOSName,
                clientVersion,
                null, 
                null, 
                null,
                null));

        // caching update
        this.proctoringCacheService.evictSession(sessionUUID);

        return result;
    }

}
