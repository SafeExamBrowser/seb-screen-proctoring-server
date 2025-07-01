/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model.user;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.ethz.seb.sps.domain.Domain.ENTITY_PRIVILEGE;
import ch.ethz.seb.sps.domain.model.EntityType;;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EntityPrivilege {

    @JsonProperty(ENTITY_PRIVILEGE.ATTR_ID)
    public final Long id;

    @JsonProperty(ENTITY_PRIVILEGE.ATTR_ENTITY_TYPE)
    public final EntityType entityType;

    @JsonProperty(ENTITY_PRIVILEGE.ATTR_ENTITY_ID)
    public final Long entityId;

    @JsonProperty(ENTITY_PRIVILEGE.ATTR_USER_UUID)
    public final String userUUID;

    @JsonProperty(ENTITY_PRIVILEGE.ATTR_PRIVILEGES)
    public final String privileges;

    @JsonCreator
    public EntityPrivilege(
            @JsonProperty(ENTITY_PRIVILEGE.ATTR_ID) final Long id,
            @JsonProperty(ENTITY_PRIVILEGE.ATTR_ENTITY_TYPE) final EntityType entityType,
            @JsonProperty(ENTITY_PRIVILEGE.ATTR_ENTITY_ID) final Long entityId,
            @JsonProperty(ENTITY_PRIVILEGE.ATTR_USER_UUID) final String userUUID,
            @JsonProperty(ENTITY_PRIVILEGE.ATTR_PRIVILEGES) final String privileges) {

        this.id = id;
        this.entityType = entityType;
        this.entityId = entityId;
        this.userUUID = userUUID;
        this.privileges = privileges;
    }

    public Long getId() {
        return this.id;
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    public Long getEntityId() {
        return this.entityId;
    }

    public String getUserUUID() {
        return this.userUUID;
    }

    public String getPrivileges() {
        return this.privileges;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final EntityPrivilege other = (EntityPrivilege) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "EntityPrivilege [id=" + this.id +
                ", entityType=" + this.entityType +
                ", entityId=" + this.entityId +
                ", userUUID=" + this.userUUID +
                ", privileges=" + this.privileges +
                "]";
    }

}
