/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer;

import java.util.Collection;

import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.service.Exam;
import ch.ethz.seb.sps.domain.model.service.Session;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import ch.ethz.seb.sps.utils.Result;

public interface SessionService {

    Result<Session> createNewSession(
            String groupUUID,
            String userSessionName,
            String clientIP,
            String clientMachineName,
            String clientOSName,
            String clientVersion,
            ImageFormat imageFormat);

    Result<Session> updateSessionData(
            String sessionUUID,
            String userSessionName,
            String clientIP,
            String clientMachineName,
            String clientOSName,
            String clientVersion);

    void closeSession(String sessionUUID);

    Result<Collection<EntityKey>> closeAllSessions(Collection<EntityKey> groupKeys);

    boolean hasAnySessionDataForExam(String examUUID);

    boolean hasAnySessionDataForGroup(String groupUUID);
}
