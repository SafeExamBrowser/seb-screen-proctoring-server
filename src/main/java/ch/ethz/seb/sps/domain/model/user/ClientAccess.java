/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model.user;

import java.util.Collection;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.ethz.seb.sps.domain.Domain.CLIENT_ACCESS;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.OwnedEntity;
import ch.ethz.seb.sps.domain.model.WithEntityPrivileges;
import ch.ethz.seb.sps.domain.model.WithLifeCycle;
import ch.ethz.seb.sps.domain.model.WithNameDescription;
import ch.ethz.seb.sps.utils.Utils;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientAccess implements Entity, OwnedEntity, WithNameDescription, WithEntityPrivileges, WithLifeCycle {

    @JsonProperty(API.PARAM_ENTITY_TYPE)
    public final EntityType entityType = EntityType.CLIENT_ACCESS;

    @JsonProperty(CLIENT_ACCESS.ATTR_ID)
    public final Long id;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(CLIENT_ACCESS.ATTR_UUID)
    public final String uuid;

    @JsonProperty(CLIENT_ACCESS.ATTR_NAME)
    @NotNull(message = "clientaccess:name:notNull")
    @Size(min = 3, max = 255, message = "clientconfig:name:size:{min}:{max}:${validatedValue}")
    public final String name;

    @JsonProperty(CLIENT_ACCESS.ATTR_DESCRIPTION)
    @Size(max = 4000, message = "clientaccess:description:size:{max}:${validatedValue}")
    public final String description;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(CLIENT_ACCESS.ATTR_CLIENT_NAME)
    public final String clientId;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(CLIENT_ACCESS.ATTR_CLIENT_SECRET)
    public final String clientSecret;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(CLIENT_ACCESS.ATTR_OWNER)
    public final String owner;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(CLIENT_ACCESS.ATTR_CREATION_TIME)
    public final Long creationTime;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(CLIENT_ACCESS.ATTR_LAST_UPDATE_TIME)
    public final Long lastUpdateTime;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(CLIENT_ACCESS.ATTR_TERMINATION_TIME)
    public final Long terminationTime;

    @JsonIgnore
    public final Collection<EntityPrivilege> entityPrivileges;

    @JsonCreator
    public ClientAccess(
            @JsonProperty(CLIENT_ACCESS.ATTR_ID) final Long id,
            @JsonProperty(CLIENT_ACCESS.ATTR_UUID) final String uuid,
            @JsonProperty(CLIENT_ACCESS.ATTR_NAME) final String name,
            @JsonProperty(CLIENT_ACCESS.ATTR_DESCRIPTION) final String description,
            @JsonProperty(CLIENT_ACCESS.ATTR_CLIENT_NAME) final String clientId,
            @JsonProperty(CLIENT_ACCESS.ATTR_CLIENT_SECRET) final String clientSecret,
            @JsonProperty(CLIENT_ACCESS.ATTR_OWNER) final String owner,
            @JsonProperty(CLIENT_ACCESS.ATTR_CREATION_TIME) final Long creationTime,
            @JsonProperty(CLIENT_ACCESS.ATTR_LAST_UPDATE_TIME) final Long lastUpdateTime,
            @JsonProperty(CLIENT_ACCESS.ATTR_TERMINATION_TIME) final Long terminationTime,

            final Collection<EntityPrivilege> entityPrivileges) {

        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.owner = owner;
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.terminationTime = terminationTime;
        this.entityPrivileges = Utils.immutableCollectionOf(entityPrivileges);
    }

    @Override
    public String getModelId() {
        return (this.uuid != null)
                ? this.uuid
                : (this.id != null)
                        ? String.valueOf(this.id)
                        : null;
    }

    public String getUuid() {
        return this.uuid;
    }

    @Override
    public EntityType entityType() {
        return this.entityType;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    public String getClientId() {
        return this.clientId;
    }

    public String getClientSecret() {
        return this.clientSecret;
    }

    @Override
    public String getOwner() {
        return this.owner;
    }

    @Override
    public Collection<EntityPrivilege> getEntityPrivileges() {
        return this.entityPrivileges;
    }

    @Override
    public Long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public Long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    @Override
    public Long getTerminationTime() {
        return this.terminationTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.entityType, this.id);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ClientAccess other = (ClientAccess) obj;
        return this.entityType == other.entityType && Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("ClientAccess [entityType=");
        builder.append(this.entityType);
        builder.append(", id=");
        builder.append(this.id);
        builder.append(", name=");
        builder.append(this.name);
        builder.append(", description=");
        builder.append(this.description);
        builder.append(", clientId=");
        builder.append(this.clientId);
        builder.append(", owner=");
        builder.append(this.owner);
        builder.append(", entityPrivileges=");
        builder.append(this.entityPrivileges);
        builder.append(", creationTime=");
        builder.append(this.creationTime);
        builder.append(", lastUpdateTime=");
        builder.append(this.lastUpdateTime);
        builder.append(", terminationTime=");
        builder.append(this.terminationTime);
        builder.append("]");
        return builder.toString();
    }

}
