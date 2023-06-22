/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao;

import java.util.Collection;

import ch.ethz.seb.sps.domain.model.EntityPrivilege;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.utils.Result;

public interface EntityPrivilegeDAO {

    Result<Collection<EntityPrivilege>> getEntityPrivileges(
            EntityType type,
            Long entityId);

    Result<Collection<EntityPrivilege>> savePut(
            EntityType type,
            Long entityId,
            Collection<EntityPrivilege> entityPrivileges);

}
