/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import java.util.UUID;

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

    private final GroupDAO groupDAO;
    private final SessionDAO sessionDAO;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ProctoringCacheService proctoringCacheService;

    public SessionServiceImpl(
            final GroupDAO groupDAO,
            final SessionDAO sessionDAO,
            final ApplicationEventPublisher applicationEventPublisher,
            final ProctoringCacheService proctoringCacheService) {

        this.groupDAO = groupDAO;
        this.sessionDAO = sessionDAO;
        this.applicationEventPublisher = applicationEventPublisher;
        this.proctoringCacheService = proctoringCacheService;
    }

    @Override
    public Result<Session> createNewSession(
            final String groupUUID,
            final String userSessionName,
            final String clientIP,
            final String clientMachineName,
            final String clientOSName,
            final String clientVersion,
            final ImageFormat imageFormat,
            final boolean createGroup) {

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
    public Result<String> closeSession(final String sessionUUID) {
        return this.sessionDAO.byModelId(sessionUUID)
                .map(session -> {
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
                    return sessionUUID;
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
                null, null, null, null));

        // caching update
        this.proctoringCacheService.evictSession(sessionUUID);

        return result;
    }

}
