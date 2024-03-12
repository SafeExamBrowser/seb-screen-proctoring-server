/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface WithLifeCycle extends Activatable {

    String FILTER_ATTR_CREATTION_TIME = "creationTime";

    Long getCreationTime();

    Long getLastUpdateTime();

    Long getTerminationTime();

    @Override
    @JsonIgnore
    default boolean isActive() {
        return getTerminationTime() == null;
    }

}