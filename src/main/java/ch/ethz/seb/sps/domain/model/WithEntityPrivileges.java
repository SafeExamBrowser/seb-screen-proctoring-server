/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model;

import java.util.Collection;
import java.util.Objects;

import ch.ethz.seb.sps.domain.api.API.PrivilegeType;
import ch.ethz.seb.sps.domain.model.user.EntityPrivilege;

public interface WithEntityPrivileges {

    public static final String ATTR_ENTITY_PRIVILEGES = "entityPrivileges";

    String getOwner();

    Collection<EntityPrivilege> getEntityPrivileges();

    static boolean hasReadAccess(final WithEntityPrivileges privileges, final String userUUID) {
        if (Objects.equals(userUUID, privileges.getOwner())) {
            return true;
        }
        return hasReadAccess(privileges.getEntityPrivileges(), userUUID);
    }

    static boolean hasReadAccess(final Collection<EntityPrivilege> privileges, final String userUUID) {
        return hasAccess(privileges, userUUID, PrivilegeType.READ);
    }

    static boolean hasModifyAccess(final WithEntityPrivileges privileges, final String userUUID) {
        if (Objects.equals(userUUID, privileges.getOwner())) {
            return true;
        }
        return hasModifyAccess(privileges.getEntityPrivileges(), userUUID);
    }

    static boolean hasModifyAccess(final Collection<EntityPrivilege> privileges, final String userUUID) {
        return hasAccess(privileges, userUUID, PrivilegeType.MODIFY);
    }

    static boolean hasWriteAccess(final WithEntityPrivileges privileges, final String userUUID) {
        if (Objects.equals(userUUID, privileges.getOwner())) {
            return true;
        }
        return hasWriteAccess(privileges.getEntityPrivileges(), userUUID);
    }

    static boolean hasWriteAccess(final Collection<EntityPrivilege> privileges, final String userUUID) {
        return hasAccess(privileges, userUUID, PrivilegeType.WRITE);
    }

    static boolean hasAccess(
            final Collection<EntityPrivilege> privileges,
            final String userUUID,
            final PrivilegeType flag) {

        if (userUUID == null) {
            return false;
        }

        return privileges.stream()
                .filter(ep -> Objects.equals(userUUID, ep.userUUID))
                .filter(ep -> ep.privileges.contains(flag.flag))
                .findAny()
                .isPresent();
    }

}
