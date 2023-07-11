/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model;

public interface WithLifeCycle extends Activatable {

    String FILTER_ATTR_CREATTION_TIME = "creationTime";

    Long getCreationTime();

    Long getLastUpdateTime();

    Long getTerminationTime();

    @Override
    default boolean isActive() {
        return getTerminationTime() == null;
    }

}