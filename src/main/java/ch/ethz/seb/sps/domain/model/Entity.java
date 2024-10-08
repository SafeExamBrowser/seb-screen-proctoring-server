/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/** Defines generic interface for all types of Entity. */
public interface Entity extends ModelIdAware {

    String FILTER_ATTR_ACTIVE = "active";
    String FILTER_ATTR_NAME = "name";

    /** Get the primary key of the entity record on persistent store if available.
     *
     * @return the primary key of the entity record on persistent store if available. */
    @JsonIgnore
    Long getId();

    /** Get the name of the entity
     *
     * @return the name of the entity */
    @JsonIgnore
    String getName();

    /** Get an unique EntityKey for the entity consisting of the model identifier of the entity
     * and the type of the entity.
     *
     * @return unique EntityKey for the entity */
    @JsonIgnore
    default EntityKey getEntityKey() {
        final String modelId = getModelId();
        if (modelId == null) {
            return null;
        }
        return new EntityKey(modelId, entityType());
    }

    /** Get the type of the entity.
     *
     * @return the type of the entity */
    @JsonIgnore
    EntityType entityType();

    /** Creates an EntityName instance from this Entity instance.
     *
     * @return EntityName instance created from given Entity */
    default EntityName toName() {
        return new EntityName(
                this.getModelId(),
                this.entityType(),
                this.getName());
    }

    /** This can be overwritten if an entity contains security sensitive data
     * Returns a representation of the entity that has no security sensitive data
     * and an be print out to user logs or error messages
     *
     * @return representation of the entity that has no security sensitive data */

    default Entity printSecureCopy() {
        return this;
    }

}
