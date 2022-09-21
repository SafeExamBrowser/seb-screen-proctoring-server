/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.batis.dao;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import ch.ethz.seb.sps.domain.model.user.ServerUser;
import ch.ethz.seb.sps.domain.model.user.UserMod;
import ch.ethz.seb.sps.server.servicelayer.dao.UserDAO;
import ch.ethz.seb.sps.utils.Result;

@Service
@ConditionalOnExpression("'${sps.data.store.adapter}'.equals('FULL_RDBMS') or '${sps.data.store.adapter}'.equals('FILESYS_RDBMS')")
public class UserDAOBatis implements UserDAO {

    @Override
    public Result<ServerUser> sebServerAdminByUsername(final String adminName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Result<ServerUser> createNew(final UserMod userMod) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void changePassword(final String modelId, final CharSequence generateAdminPassword) {
        // TODO Auto-generated method stub

    }

}
