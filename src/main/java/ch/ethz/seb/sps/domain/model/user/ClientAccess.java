/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model.user;

import java.util.Objects;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.ethz.seb.sps.domain.Domain.CLIENT_ACCESS;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.EntityType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientAccess implements Entity {

    @JsonProperty(API.PARAM_ENTITY_TYPE)
    public final EntityType entityType = EntityType.CLIENT_ACCESS;

    @JsonProperty(CLIENT_ACCESS.ATTR_ID)
    public final Long id;

    @JsonProperty(CLIENT_ACCESS.ATTR_CLIENT_ID)
    public final String clientId;

    @JsonProperty(CLIENT_ACCESS.ATTR_CLIENT_SECRET)
    public final String clientSecret;

    @JsonProperty(CLIENT_ACCESS.ATTR_CREATION_DATE)
    public final DateTime creationDate;

    @JsonProperty(CLIENT_ACCESS.ATTR_ACTIVE)
    public final Boolean active;

    @JsonCreator
    public ClientAccess(
            @JsonProperty(CLIENT_ACCESS.ATTR_ID) final Long id,
            @JsonProperty(CLIENT_ACCESS.ATTR_CLIENT_ID) final String clientId,
            @JsonProperty(CLIENT_ACCESS.ATTR_CLIENT_SECRET) final String clientSecret,
            @JsonProperty(CLIENT_ACCESS.ATTR_CREATION_DATE) final DateTime creationDate,
            @JsonProperty(CLIENT_ACCESS.ATTR_ACTIVE) final Boolean active) {

        this.id = id;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.creationDate = creationDate;
        this.active = active;
    }

    @Override
    public String getModelId() {
        return (this.id != null)
                ? String.valueOf(this.id)
                : null;
    }

    @Override
    public EntityType entityType() {
        return this.entityType;
    }

    @Override
    public String getName() {
        return this.clientId;
    }

    public Long getId() {
        return this.id;
    }

    public String getClientId() {
        return this.clientId;
    }

    public String getClientSecret() {
        return this.clientSecret;
    }

    public DateTime getCreationDate() {
        return this.creationDate;
    }

    public Boolean getActive() {
        return this.active;
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
        builder.append(", clientId=");
        builder.append(this.clientId);
        builder.append(", creationDate=");
        builder.append(this.creationDate);
        builder.append(", active=");
        builder.append(this.active);
        builder.append("]");
        return builder.toString();
    }

}
