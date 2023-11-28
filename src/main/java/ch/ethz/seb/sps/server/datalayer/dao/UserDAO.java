/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao;

import ch.ethz.seb.sps.domain.model.user.ServerUser;
import ch.ethz.seb.sps.domain.model.user.UserInfo;
import ch.ethz.seb.sps.domain.model.user.UserMod;
import ch.ethz.seb.sps.utils.Result;

public interface UserDAO extends ActivatableEntityDAO<UserInfo, UserMod> {

    Result<Long> getUserIdByUUID(String userUUID);

    Result<ServerUser> byUsername(String adminName);

    Result<UserInfo> changePassword(String modelId, CharSequence newPassword);

    /** This can be used to synchronize a user account data with given userData.
     * If the user account with given name exists already on this service, the
     * given attributes are saved for the user.
     * If there is no user account yet on the system, it creates a new one with
     * the given attributes.
     *
     * @param userData the user account data to synchronize
     * @return Result refer to the new user account data or to an error when happened */
    Result<UserInfo> synchronizeUserAccount(UserMod userData);

}
