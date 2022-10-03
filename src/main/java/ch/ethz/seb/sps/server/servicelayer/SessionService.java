/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer;

import ch.ethz.seb.sps.utils.Result;

public interface SessionService {

    default Result<String> createNewSession(final String groupUUID) {
        return createNewSession(groupUUID, false);
    }

    // TODO caching
    Result<String> getActiveSessions(String groupUUID);

    Result<String> createNewSession(String groupUUID, boolean createGroup);

    Result<String> closeSession(String sessionUUID);

}
