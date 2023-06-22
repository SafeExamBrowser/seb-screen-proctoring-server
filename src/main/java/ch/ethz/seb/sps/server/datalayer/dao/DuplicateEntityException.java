/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import ch.ethz.seb.sps.domain.model.EntityType;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicateEntityException extends RuntimeException {

    private static final long serialVersionUID = 7887765453632691440L;
    public final EntityType entityType;
    public final String fieldName;
    public final String message;

    public DuplicateEntityException(
            final EntityType entityType,
            final String fieldName,
            final String message) {

        super("Duplicate resource " + entityType + " detected: " + message);

        this.entityType = entityType;
        this.fieldName = fieldName;
        this.message = message;
    }

}
