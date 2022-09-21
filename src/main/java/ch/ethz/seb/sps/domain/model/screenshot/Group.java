/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model.screenshot;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.ethz.seb.sps.domain.Domain.GROUP;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.EntityType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Group implements Entity {

    @JsonProperty(API.PARAM_ENTITY_TYPE)
    public final EntityType entityType = EntityType.GROUP;

    @JsonProperty(GROUP.ATTR_ID)
    public final Long id;

    @JsonProperty(GROUP.ATTR_UUID)
    public final String uuid;

    @JsonProperty(GROUP.ATTR_NAME)
    public final String name;

    @JsonCreator
    public Group(
            @JsonProperty(GROUP.ATTR_ID) final Long id,
            @JsonProperty(GROUP.ATTR_UUID) final String uuid,
            @JsonProperty(GROUP.ATTR_NAME) final String name) {

        this.id = id;
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public String getModelId() {
        return this.uuid;
    }

    @Override
    public EntityType entityType() {
        return this.entityType;
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
        final Group other = (Group) obj;
        return this.entityType == other.entityType && Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Group [entityType=");
        builder.append(this.entityType);
        builder.append(", id=");
        builder.append(this.id);
        builder.append(", uuid=");
        builder.append(this.uuid);
        builder.append(", name=");
        builder.append(this.name);
        builder.append("]");
        return builder.toString();
    }

}
