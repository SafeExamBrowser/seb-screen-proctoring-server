/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao;

import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.service.Group;
import ch.ethz.seb.sps.domain.model.service.GroupViewData;
import ch.ethz.seb.sps.utils.Result;

import java.util.Collection;

public interface GroupDAO extends ActivatableEntityDAO<Group, Group> {

    boolean existsByUUID(String groupUUID);

    Result<Group> createNew(String name);

    Result<Collection<Group>> pksByGroupName(final FilterMap filterMap);

    Result<Collection<GroupViewData>> getGroupsWithExamData(final FilterMap filterMap);
}
