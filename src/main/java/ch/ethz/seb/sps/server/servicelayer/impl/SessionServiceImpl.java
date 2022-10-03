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

import ch.ethz.seb.sps.domain.model.screenshot.Session;
import ch.ethz.seb.sps.server.servicelayer.SessionOnClosingEvent;
import ch.ethz.seb.sps.server.servicelayer.SessionService;
import ch.ethz.seb.sps.server.servicelayer.dao.GroupDAO;
import ch.ethz.seb.sps.server.servicelayer.dao.SessionDAO;
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
    public Result<String> createNewSession(final String groupUUID, final boolean createGroup) {

        return Result.tryCatch(() -> {
            final String newSessionId = UUID.randomUUID().toString();

            // check if group exists
            if (!this.groupDAO.existsByUUID(groupUUID)) {
                this.groupDAO.createNew(groupUUID, groupUUID)
                        .getOrThrow();
            }

            // and get id
            final Long groupId = this.groupDAO.getGroupIdByUUID(groupUUID)
                    .getOrThrow();

            final Session session = this.sessionDAO
                    .createNew(groupId, newSessionId, newSessionId)
                    .getOrThrow();

            return session.uuid;
        });
    }

    @Override
    public Result<String> closeSession(final String sessionUUID) {
        this.applicationEventPublisher.publishEvent(new SessionOnClosingEvent(sessionUUID));
        return this.sessionDAO.closeSession(sessionUUID);
    }

}
