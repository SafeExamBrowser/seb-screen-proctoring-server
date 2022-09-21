/*
 * Copyright (c) 2018 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.api;

public final class API {

    public enum UserRoles {
        ADMIN,
        PROCTOR,
        SEBCLIENT
    }

    public static final String PARAM_MODEL_ID = "modelId";
    public static final String PARAM_ENTITY_TYPE = "entityType";

}
