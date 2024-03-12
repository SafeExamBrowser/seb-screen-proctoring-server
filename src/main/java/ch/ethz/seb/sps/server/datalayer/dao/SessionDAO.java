/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao;

import java.util.Collection;
import java.util.List;

import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.service.Session;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import ch.ethz.seb.sps.utils.Result;

public interface SessionDAO extends EntityDAO<Session, Session> {

    Result<Session> createNew(
            String groupUUID,
            String uuid,
            String userSessionName,
            String clientIP,
            String clientMachineName,
            String clientOSName,
            String clientVersion,
            ImageFormat imageFormat);

    Result<Collection<String>> allSessionUUIDs(Long groupId);

    Result<String> closeSession(String sessionUUID);

    Result<Collection<EntityKey>> closeAllSessionsForGroup(Long groupPK);

    Result<Collection<EntityKey>> deleteAllForGroups(final List<Long> groupPKs);

    Long getNumberOfScreenshots(String uuid, FilterMap filterMap);

}
