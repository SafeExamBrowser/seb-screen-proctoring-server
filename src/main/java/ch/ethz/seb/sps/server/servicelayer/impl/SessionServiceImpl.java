/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import java.util.UUID;

import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.APIErrorException;
import ch.ethz.seb.sps.domain.model.screenshot.Session;
import ch.ethz.seb.sps.domain.model.screenshot.Session.ImageFormat;
import ch.ethz.seb.sps.server.datalayer.dao.GroupDAO;
import ch.ethz.seb.sps.server.datalayer.dao.SessionDAO;
import ch.ethz.seb.sps.server.servicelayer.SessionOnClosingEvent;
import ch.ethz.seb.sps.server.servicelayer.SessionService;
import ch.ethz.seb.sps.utils.Constants;
import ch.ethz.seb.sps.utils.Result;

@Service
public class SessionServiceImpl implements SessionService {

    private final GroupDAO groupDAO;
    private final SessionDAO sessionDAO;
    private final ApplicationEventPublisher applicationEventPublisher;

    public SessionServiceImpl(
            final GroupDAO groupDAO,
            final SessionDAO sessionDAO,
            final ApplicationEventPublisher applicationEventPublisher) {

        this.groupDAO = groupDAO;
        this.sessionDAO = sessionDAO;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public Result<String> getActiveSessions(final String groupUUID) {
        return this.groupDAO.getGroupIdByUUID(groupUUID)
                .map(groupId -> StringUtils.join(
                        this.sessionDAO.allActiveSessionIds(groupId).getOrThrow(),
                        Constants.LIST_SEPARATOR_CHAR));
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

            // and get id
            final Long groupId = this.groupDAO.getGroupIdByUUID(groupUUID)
                    .getOrThrow();

            final Session session = this.sessionDAO
                    .createNew(
                            groupId,
                            newSessionId,
                            userSessionName,
                            clientIP,
                            clientMachineName,
                            clientOSName,
                            clientVersion,
                            imageFormat)
                    .getOrThrow();

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
                .byUUID(sessionUUID)
                .map(this::checkUpdateIntegrity)
                .flatMap(session -> this.sessionDAO.save(new Session(
                        session.id,
                        null, null,
                        userSessionName,
                        clientIP,
                        clientMachineName,
                        clientOSName,
                        clientVersion,
                        null, null, null, null)));
    }

    @Override
    public Result<String> closeSession(final String sessionUUID) {
        this.applicationEventPublisher.publishEvent(new SessionOnClosingEvent(sessionUUID));
        return this.sessionDAO.closeSession(sessionUUID);
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

}
