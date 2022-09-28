/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import ch.ethz.seb.sps.domain.model.screenshot.Session;
import ch.ethz.seb.sps.server.servicelayer.SessionService;
import ch.ethz.seb.sps.server.servicelayer.dao.GroupDAO;
import ch.ethz.seb.sps.server.servicelayer.dao.SessionDAO;
import ch.ethz.seb.sps.utils.Result;

@Service
public class SessionServiceImpl implements SessionService {

    private final GroupDAO groupDAO;
    private final SessionDAO sessionDAO;

    public SessionServiceImpl(
            final GroupDAO groupDAO,
            final SessionDAO sessionDAO) {

        this.groupDAO = groupDAO;
        this.sessionDAO = sessionDAO;
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

}
