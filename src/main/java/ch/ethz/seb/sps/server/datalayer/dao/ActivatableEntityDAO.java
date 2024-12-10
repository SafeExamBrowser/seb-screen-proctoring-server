/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao;

import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.ModelIdAware;
import ch.ethz.seb.sps.utils.Result;

/** Interface of a Data Access Object for an Entity that has activation feature.
 *
 * @param <T> the type of Entity */
public interface ActivatableEntityDAO<T extends Entity, M extends ModelIdAware> extends EntityDAO<T, M> {

    /** Set all entities referred by the given Collection of EntityKey active / inactive
     *
     * @param all The Collection of EntityKeys to set active or inactive
     * @param active The active flag
     * @return The Collection of Results refer to the EntityKey instance or refer to an error if happened */
    Result<EntityKey>  setActive(EntityKey entityKey, boolean active);

    default Result<T> setActive(final T entity, final boolean active) {
        return setActive(entity.getEntityKey(), active)
                .map(key -> byModelId(key.modelId).getOr(entity));
    }

    /** Indicates if the entity with specified model identifier is currently active
     *
     * @param modelId the model identifier of the entity
     * @return true if the entity is active, false otherwise */
    boolean isActive(String modelId);

}
