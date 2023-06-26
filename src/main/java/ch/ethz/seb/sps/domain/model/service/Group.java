/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model.service;

import java.util.Collection;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.ethz.seb.sps.domain.Domain.SEB_GROUP;
import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.EntityPrivilege;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.OwnedEntity;
import ch.ethz.seb.sps.domain.model.WithEntityPrivileges;
import ch.ethz.seb.sps.domain.model.WithLifeCycle;
import ch.ethz.seb.sps.domain.model.WithNameDescription;
import ch.ethz.seb.sps.utils.Utils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Group implements Entity, OwnedEntity, WithNameDescription, WithEntityPrivileges, WithLifeCycle {

    @JsonProperty(SEB_GROUP.ATTR_ID)
    public final Long id;

    @JsonProperty(SEB_GROUP.ATTR_UUID)
    public final String uuid;

    @JsonProperty(SEB_GROUP.ATTR_NAME)
    @NotNull(message = "clientaccess:name:notNull")
    @Size(min = 3, max = 255, message = "group:name:size:{min}:{max}:${validatedValue}")
    public final String name;

    @JsonProperty(SEB_GROUP.ATTR_DESCRIPTION)
    @Size(max = 4000, message = "group:description:size:{max}:${validatedValue}")
    public final String description;

    @JsonProperty(SEB_GROUP.ATTR_OWNER)
    public final String owner;

    @JsonProperty(SEB_GROUP.ATTR_CREATION_TIME)
    public final Long creationTime;

    @JsonProperty(SEB_GROUP.ATTR_LAST_UPDATE_TIME)
    public final Long lastUpdateTime;

    @JsonProperty(SEB_GROUP.ATTR_TERMINATION_TIME)
    public final Long terminationTime;

    @JsonProperty(WithEntityPrivileges.ATTR_ENTITY_PRIVILEGES)
    public final Collection<EntityPrivilege> entityPrivileges;

    @JsonCreator
    public Group(
            @JsonProperty(SEB_GROUP.ATTR_ID) final Long id,
            @JsonProperty(SEB_GROUP.ATTR_UUID) final String uuid,
            @JsonProperty(SEB_GROUP.ATTR_NAME) final String name,
            @JsonProperty(SEB_GROUP.ATTR_DESCRIPTION) final String description,
            @JsonProperty(SEB_GROUP.ATTR_OWNER) final String owner,
            @JsonProperty(SEB_GROUP.ATTR_CREATION_TIME) final Long creationTime,
            @JsonProperty(SEB_GROUP.ATTR_LAST_UPDATE_TIME) final Long lastUpdateTime,
            @JsonProperty(SEB_GROUP.ATTR_TERMINATION_TIME) final Long terminationTime,
            @JsonProperty(WithEntityPrivileges.ATTR_ENTITY_PRIVILEGES) final Collection<EntityPrivilege> entityPrivileges) {

        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.terminationTime = terminationTime;
        this.entityPrivileges = Utils.immutableCollectionOf(entityPrivileges);
    }

    @Override
    public String getModelId() {
        return (this.id != null)
                ? String.valueOf(this.id)
                : null;
    }

    @Override
    public EntityType entityType() {
        return EntityType.SEB_GROUP;
    }

    public Long getId() {
        return this.id;
    }

    public String getUuid() {
        return this.uuid;
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
    public String getOwner() {
        return this.owner;
    }

    @Override
    public Collection<EntityPrivilege> getEntityPrivileges() {
        return this.entityPrivileges;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Group other = (Group) obj;
        return Objects.equals(this.uuid, other.uuid);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Group [id=");
        builder.append(this.id);
        builder.append(", uuid=");
        builder.append(this.uuid);
        builder.append(", name=");
        builder.append(this.name);
        builder.append(", description=");
        builder.append(this.description);
        builder.append(", owner=");
        builder.append(this.owner);
        builder.append(", creationTime=");
        builder.append(this.creationTime);
        builder.append(", lastUpdateTime=");
        builder.append(this.lastUpdateTime);
        builder.append(", terminationTime=");
        builder.append(this.terminationTime);
        builder.append(", entityPrivileges=");
        builder.append(this.entityPrivileges);
        builder.append("]");
        return builder.toString();
    }

}
