/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer;

import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.utils.Result;

public interface BeanValidationService {

    /** Validates a given bean that is annotated with Java bean validation annotations
     * <p>
     * On validation error BeanValidationException is used to collect all validation issues
     * and report them within the Result.
     *
     * @param bean the Bean to validate
     * @return Result referring the Bean if there are no validation issues or to a BeanValidationException
     *         containing the collected validation issues */
    <T> Result<T> validateBean(T bean);

    /** Indicates whether the Entity of a given EntityKey is currently active or not.
     *
     * @param entityKey the EntityKey of the Entity to check
     * @return true if the Entity of a given EntityKey is currently active */
    boolean isActive(EntityKey entityKey);

}