/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.ModelIdAware;
import ch.ethz.seb.sps.server.datalayer.dao.EntityDAO;
import ch.ethz.seb.sps.server.servicelayer.EntityService;

@Lazy
@Service
public class EntityServiceImpl implements EntityService {

    private final Map<EntityType, EntityDAO<?, ?>> daoMapping;

    public EntityServiceImpl(final Collection<EntityDAO<?, ?>> daos) {
        this.daoMapping = new EnumMap<>(EntityType.class);
        this.daoMapping.putAll(daos.stream()
                .collect(Collectors
                        .toMap(EntityDAO::entityType, Function.identity())));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Entity, M extends ModelIdAware> EntityDAO<T, M> getEntityDAOForType(final EntityType entityType) {
        return (EntityDAO<T, M>) this.daoMapping.get(entityType);
    }

    @Override
    public Long getIdForModelId(final String modelId, final EntityType entityType) {
        return getEntityDAOForType(entityType).modelIdToPK(modelId);
    }

}
