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
import java.util.Set;

import ch.ethz.seb.sps.domain.api.API.PrivilegeType;
import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.user.EntityPrivilege;
import ch.ethz.seb.sps.utils.Result;

/** An EntityPrivilege is a privilege for a specific user on a specific entity instance.
 * EntityPrivilege maps a user UUID to a entity (type and id) and specifies a privilege for the user.
 * The PrivilegeType is either "r" for read, "m" for modify (and read) or "w" for write (and modify and read) */
public interface EntityPrivilegeDAO {

    /** Get all entity privileges for a given entity type and id.
     * 
     * @param entityType The entity type
     * @param entityId The entity id (PK)
     * @return Result refer to a collection of all EntityPrivilege for the given entity or to an error when happened.*/
    Result<Collection<EntityPrivilege>> getEntityPrivileges(EntityType entityType, Long entityId);

    /** Get all entity ids for a given user and a given PrivilegeType.
     * 
     * @param entityType The entity type
     * @param userUUID The user UUID
     * @param privilegeType The privilege type
     * @return Result refer to a set of entity ids (PKs) that the given user has the privilege for, or to an error when happened */
    Result<Set<Long>> getEntityIdsWithPrivilegeForUser(EntityType entityType, String userUUID, PrivilegeType privilegeType);

    /** Get all EntityPrivilege for a given user.
     * 
     * @param userUUID The user UUID
     * @return Result refer to the collection of EntityPrivilege the given user has or to an error when happened */
    Result<Collection<EntityPrivilege>> getEntityPrivilegesForUser(String userUUID);

    /** Add a given privilege for a user to an entity.
     * 
     * @param entityType The entity type 
     * @param entityId The entity id (PK)
     * @param userUUID The user UUID
     * @param privilegeType The type of privilege to give to the user on entity
     * @return Result refer to added EntityPrivilege or to an error when happened */
    Result<EntityPrivilege> addPrivilege(
            EntityType entityType,
            Long entityId,
            String userUUID,
            PrivilegeType privilegeType);

    /** Deletes a privilege for a user on an object.
     * 
     * @param entityType The entity type 
     * @param entityId The entity id (PK)
     * @param userUUID The user UUID
     * @return Result refer to the EntityKey of the deleted EntityPrivilege or to an error when happened*/
    Result<EntityKey> deletePrivilege(EntityType entityType, Long entityId, String userUUID);

    /** Deletes all EntityPrivileges of a specific entity.
     * 
     * @param entityType The entity type 
     * @param entityId The entity id (PK)
     * @return Result refer to a collection of all deleted EntityPrivilege keys or to an error when happened. */
    Result<Collection<EntityKey>> deleteAllPrivileges(EntityType entityType, Long entityId);

    /** Deletes all privileges for a specified user.
     * 
     * @param user_uuid The user UUID to delete all privileges for*/
    void deleteAllPrivilegesForUser(String user_uuid);

    /** Update one single EntityPrivilege.
     * 
     * @param epId EntityPrivilege id (PK)
     * @param privilegeType The PrivilegeType to update to */
    void updatePrivilege(Long epId, PrivilegeType privilegeType);

    /** Update all given EntityPrivileges to a given PrivilegeType.
     * 
     * @param epIds Collection of EntityPrivilege ids (PKs)
     * @param privilegeType The PrivilegeType to update to */
    void updatePrivileges(Collection<Long> epIds, PrivilegeType privilegeType);
}
