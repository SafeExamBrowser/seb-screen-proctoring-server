/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer;

import java.security.Principal;
import java.util.Set;

import ch.ethz.seb.sps.domain.api.API.PrivilegeType;
import ch.ethz.seb.sps.domain.api.APIErrorException;
import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.user.ServerUser;
import ch.ethz.seb.sps.domain.model.user.UserInfo;
import ch.ethz.seb.sps.domain.model.user.UserMod;
import ch.ethz.seb.sps.domain.model.user.UserPrivileges;
import ch.ethz.seb.sps.utils.Result;

public interface UserService {

    /** Use this to get the current User within a request-response thread cycle.
     *
     * @return the ServerUser instance of the current request
     * @throws IllegalStateException if no Authentication was found
     * @throws IllegalArgumentException if fromPrincipal is not able to extract the User of the Authentication
     *             instance */
    ServerUser getCurrentUser();

    default String getCurrentUserUUIDOrNull() {
        try {
            return getCurrentUser().uuid();
        } catch (final Exception e) {
            return null;
        }
    }

    /** Extracts the internal UserInfo from a given Principal.
     * <p>
     * This is attended to apply some known strategies to extract the internal user from Principal. If there is no
     * internal user found on the given Principal, a IllegalArgumentException is thrown.
     * <p>
     * If there is certainly a internal user within the given Principal but no strategy that finds it, this method can
     * be extended with the needed strategy.
     *
     * @param principal The users principal
     * @return internal User instance if it was found within the Principal and the existing strategies
     * @throws IllegalArgumentException if no internal User can be found */
    ServerUser extractFromPrincipal(final Principal principal);

    default boolean hasReadGrant(final Entity entity) {
        return hasGrant(PrivilegeType.READ, entity);
    }

    default boolean hasModifyGrant(final Entity entity) {
        return hasGrant(PrivilegeType.MODIFY, entity);
    }

    default boolean hasWriteGrant(final Entity entity) {
        return hasGrant(PrivilegeType.WRITE, entity);
    }

    default boolean hasGrant(final PrivilegeType privilegeType, final EntityType entityType) {
        return hasGrant(getCurrentUser().getUserInfo(), privilegeType, entityType);
    }

    default boolean hasGrant(final PrivilegeType privilegeType, final Entity entity) {
        return hasGrant(getCurrentUser().getUserInfo(), privilegeType, entity);
    }

    /** Check grant on privilege type for specified EntityType and for the current user.
     *
     * @param user the user to check grant for
     * @param privilegeType The privilege type to check
     * @param entityType The type of entity to check privilege on
     * @return true if there is any grant within the given context or false on deny */
    boolean hasGrant(UserInfo user, PrivilegeType privilegeType, EntityType entityType);

    /** Check grant on privilege type for specified Entity for the current user.
     *
     * @param user the user to check grant for
     * @param privilegeType The privilege type to check
     * @param entity The entity instance
     * @return true if there is any grant within the given context or false on deny */
    boolean hasGrant(UserInfo user, PrivilegeType privilegeType, Entity entity);

    /** Indicates if the given user has an owner privilege for this give entity.
     * NOTE: Owner privileges are always write privileges so an owner can do everything with an owned entity
     *
     * @param user the user to check grant for
     * @param entity The entity instance
     * @return true if the current user has owner privilege */
    boolean hasOwnerPrivilege(UserInfo user, Entity entity);

    /** Indicates if the current user has entity instance privileges for this given entity instance.
     * Entity instance privileges can be defined per entity if the entity type implements WithEntityPrivileges interface
     *
     * @param user the user to check grant for
     * @param entity The entity instance
     * @param privilegeType The privilege type to check
     * @return true if current user has */
    boolean hasInstancePrivilege(UserInfo user, Entity entity, PrivilegeType privilegeType);

    default <T extends Entity> T checkRead(final T entity) {
        check(PrivilegeType.READ, entity);
        return entity;
    }

    default <T extends Entity> T checkModify(final T entity) {
        check(PrivilegeType.MODIFY, entity);
        return entity;
    }

    default <T extends Entity> T checkWrite(final T entity) {
        check(PrivilegeType.WRITE, entity);
        return entity;
    }

    /** Check grant by using corresponding hasGrant(XY) method and throws PermissionDeniedException
     * on deny.
     *
     * @param privilegeType the privilege type to check
     * @param entityType the type of the entity to check the given privilege type on */
    default void check(final PrivilegeType privilegeType, final EntityType entityType) {
        if (hasGrant(privilegeType, entityType)) {
            return;
        }

        throw APIErrorException.ofPermissionDenied(entityType, privilegeType, getCurrentUser().getUserInfo());
    }

    /** Check grant by using corresponding hasGrant(XY) method and throws PermissionDeniedException
     * on deny.
     *
     * @param privilegeType the privilege type to check
     * @param entity the entity */
    default <T extends Entity> T check(final PrivilegeType privilegeType, final T entity) {
        if (hasGrant(privilegeType, entity)) {
            return entity;
        }

        throw APIErrorException.ofPermissionDenied(
                entity.entityType(),
                privilegeType,
                getCurrentUser().getUserInfo());
    }

    /** Get a collection of entity id's/pk's that has an entity based read privilege (EntityPrivilege).
     *
     * @param entityType The type of the entity
     * @return Result refer to the resulting collection or to an error when happened */
    Result<Set<Long>> getIdsWithReadEntityPrivilege(EntityType entityType);

    void applyWriteEntityPrivilegeGrant(EntityType entityType, Long entityId, String userUUID);

    Result<UserInfo> synchronizeUserAccount(UserMod userMod);

    Result<UserPrivileges> getUserPrivileges(String userUUID);

}
