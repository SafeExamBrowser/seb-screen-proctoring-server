/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao;

import java.util.Collection;
import java.util.List;

import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.service.Group;
import ch.ethz.seb.sps.domain.model.service.GroupViewData;
import ch.ethz.seb.sps.utils.Result;

public interface GroupDAO extends ActivatableEntityDAO<Group, Group> {

    boolean existsByUUID(String groupUUID);

    Result<Group> createNew(String name);

    Result<Collection<Group>> byGroupName(final FilterMap filterMap);

    Result<Collection<GroupViewData>> getGroupsWithExamData(final FilterMap filterMap);

    Result<Collection<EntityKey>> applyActivationForAllOfExam(Long examId, boolean activation);

    Result<Collection<EntityKey>> deleteAllForExams(final List<Long> examPKs);

}
