/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao;

import java.util.Collection;

import ch.ethz.seb.sps.domain.model.service.Session;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import ch.ethz.seb.sps.utils.Result;

public interface SessionDAO extends EntityDAO<Session, Session> {

//    Result<Session> byUUID(String sessionUUID);
//
//    Result<Long> pkByUUID(String sessionUUID);

    Result<Session> createNew(
            String groupUUID,
            String uuid,
            String userSessionName,
            String clientIP,
            String clientMachineName,
            String clientOSName,
            String clientVersion,
            ImageFormat imageFormat);

    //Result<Collection<String>> allActiveSessionIds(String groupUUID);

    Result<Collection<String>> allSessionUUIDs(Long groupId);

    Result<String> closeSession(String sessionUUID);

}
