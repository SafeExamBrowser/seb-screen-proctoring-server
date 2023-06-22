/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer;

import ch.ethz.seb.sps.domain.model.screenshot.Session;
import ch.ethz.seb.sps.domain.model.screenshot.Session.ImageFormat;
import ch.ethz.seb.sps.utils.Result;

public interface SessionService {

    default Result<Session> createNewSession(
            final String groupUUID,
            final String userSessionName,
            final String clientIP,
            final String clientMachineName,
            final String clientOSName,
            final String clientVersion,
            final ImageFormat imageFormat) {

        return createNewSession(
                groupUUID,
                userSessionName,
                clientIP,
                clientMachineName,
                clientOSName,
                clientVersion,
                imageFormat,
                false);
    }

    Result<Session> createNewSession(
            String groupUUID,
            String userSessionName,
            String clientIP,
            String clientMachineName,
            String clientOSName,
            String clientVersion,
            ImageFormat imageFormat,
            boolean createGroup);

    Result<Session> updateSessionData(
            String sessionUUID,
            String userSessionName,
            String clientIP,
            String clientMachineName,
            String clientOSName,
            String clientVersion);

    /** Get comma separated String of all active session UUIDs
     *
     * @param groupUUID The group id
     * @return Result refer to the result or to an error if happened */
    // TODO caching
    Result<String> getActiveSessions(String groupUUID);

    Result<String> closeSession(String sessionUUID);

}
