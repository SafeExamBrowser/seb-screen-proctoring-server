/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao;

import java.util.Collection;
import java.util.Set;

import ch.ethz.seb.sps.domain.api.API.PrivilegeType;
import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.user.EntityPrivilege;
import ch.ethz.seb.sps.utils.Result;

public interface EntityPrivilegeDAO {

    Result<Collection<EntityPrivilege>> getEntityPrivileges(
            EntityType type,
            Long entityId);

    Result<Set<Long>> getEntityIdsWithPrivilegeForUser(
            EntityType type,
            String userUUID,
            PrivilegeType privilegeType);

    Result<Collection<EntityPrivilege>> getEntityPrivilegesForUser(String userUUID);

    Result<EntityPrivilege> addPrivilege(
            EntityType type,
            Long entityId,
            String userUUID,
            PrivilegeType privilegeType);

    Result<EntityKey> deletePrivilege(
            EntityType type,
            Long entityId,
            String userUUID);

    Result<Collection<EntityKey>> deleteAllPrivileges(EntityType type, Long entityId);

}
