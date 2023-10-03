/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model.user;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.ethz.seb.sps.domain.Domain.USER;
import ch.ethz.seb.sps.domain.api.API.PrivilegeType;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.utils.Utils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserPrivileges {

    public static final String ATTR_TYPE_PRIVILEGES = "typePrivileges";
    public static final String ATTR_ENTITY_PRIVILEGES = "entityPrivileges";

    @JsonProperty(USER.ATTR_UUID)
    public final String userUUID;

    @JsonProperty(ATTR_TYPE_PRIVILEGES)
    public final Map<EntityType, PrivilegeType> typePrivileges;

    @JsonProperty(ATTR_ENTITY_PRIVILEGES)
    public final Collection<EntityPrivilege> entityPrivileges;

    @JsonCreator
    public UserPrivileges(
            @JsonProperty(USER.ATTR_UUID) final String userUUID,
            @JsonProperty(ATTR_TYPE_PRIVILEGES) final Map<EntityType, PrivilegeType> typePrivileges,
            @JsonProperty(ATTR_ENTITY_PRIVILEGES) final Collection<EntityPrivilege> entityPrivileges) {

        this.userUUID = userUUID;
        this.typePrivileges = CollectionUtils.isEmpty(typePrivileges)
                ? new EnumMap<>(EntityType.class)
                : new EnumMap<>(typePrivileges);
        this.entityPrivileges = Utils.immutableCollectionOf(entityPrivileges);
    }

    public String getUserUUID() {
        return this.userUUID;
    }

    public Map<EntityType, PrivilegeType> getTypePrivileges() {
        return this.typePrivileges;
    }

    public Collection<EntityPrivilege> getEntityPrivileges() {
        return this.entityPrivileges;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.userUUID);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final UserPrivileges other = (UserPrivileges) obj;
        return Objects.equals(this.userUUID, other.userUUID);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("UserPrivileges [userUUID=");
        builder.append(this.userUUID);
        builder.append(", typePrivileges=");
        builder.append(this.typePrivileges);
        builder.append(", entityPrivileges=");
        builder.append(this.entityPrivileges);
        builder.append("]");
        return builder.toString();
    }

}
