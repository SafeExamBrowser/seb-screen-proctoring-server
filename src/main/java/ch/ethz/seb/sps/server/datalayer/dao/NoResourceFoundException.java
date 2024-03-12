/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import ch.ethz.seb.sps.domain.model.EntityType;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoResourceFoundException extends RuntimeException {

    private static final long serialVersionUID = 4347712679241097195L;
    public final EntityType entityType;

    public NoResourceFoundException(final EntityType entityType, final String message) {
        super("Resource " + entityType + " not found: " + message);
        this.entityType = entityType;
    }

}
