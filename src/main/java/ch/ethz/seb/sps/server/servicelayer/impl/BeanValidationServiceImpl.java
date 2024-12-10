/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Validator;

import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.server.datalayer.dao.ActivatableEntityDAO;
import ch.ethz.seb.sps.server.datalayer.dao.EntityDAO;
import ch.ethz.seb.sps.server.servicelayer.BeanValidationException;
import ch.ethz.seb.sps.server.servicelayer.BeanValidationService;
import ch.ethz.seb.sps.utils.Result;

/** This service can be used to 'manually' validate a Bean that is annotated within bean
 * validation annotations.
 * <p>
 * On validation error BeanValidationException is used to collect all validation issues
 * and report them within the Result. */
@Service
public class BeanValidationServiceImpl implements BeanValidationService {

    private final Validator validator;
    private final Map<EntityType, ActivatableEntityDAO<?, ?>> activatableDAOs;

    public BeanValidationServiceImpl(
            final Validator validator,
            final Collection<ActivatableEntityDAO<?, ?>> activatableDAOs) {

        this.validator = validator;
        this.activatableDAOs = activatableDAOs
                .stream()
                .collect(Collectors.toMap(
                        EntityDAO::entityType,
                        Function.identity()));
    }

    /** Validates a given bean that is annotated with Java bean validation annotations
     *
     * On validation error BeanValidationException is used to collect all validation issues
     * and report them within the Result.
     *
     * @param bean the Bean to validate
     * @return Result referring the Bean if there are no validation issues or to a BeanValidationException
     *         containing the collected validation issues */
    @Override
    public <T> Result<T> validateBean(final T bean) {
        final DirectFieldBindingResult errors = new DirectFieldBindingResult(bean, "");
        this.validator.validate(bean, errors);
        if (errors.hasErrors()) {
            return Result.ofError(new BeanValidationException(errors));
        }

        return Result.of(bean);
    }

    /** Indicates whether the Entity of a given EntityKey is currently active or not.
     *
     * @param entityKey the EntityKey of the Entity to check
     * @return true if the Entity of a given EntityKey is currently active */
    @Override
    public boolean isActive(final EntityKey entityKey) {
        final ActivatableEntityDAO<?, ?> activatableEntityDAO = this.activatableDAOs.get(entityKey.entityType);
        if (activatableEntityDAO == null) {
            return false;
        }

        return activatableEntityDAO.isActive(entityKey.modelId);
    }

}
