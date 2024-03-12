/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer;

import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.ModelIdAware;
import ch.ethz.seb.sps.server.datalayer.dao.EntityDAO;

public interface EntityService {

    <T extends Entity, M extends ModelIdAware> EntityDAO<T, M> getEntityDAOForType(EntityType entityType);

    Long getIdForModelId(String modelId, EntityType entityType);

}
