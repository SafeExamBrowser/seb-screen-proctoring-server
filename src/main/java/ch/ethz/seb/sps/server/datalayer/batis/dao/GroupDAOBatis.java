/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.batis.dao;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import ch.ethz.seb.sps.server.servicelayer.dao.GroupDAO;

@Service
@ConditionalOnExpression("'${sps.data.store.adapter}'.equals('FULL_RDBMS') or '${sps.data.store.adapter}'.equals('FILESYS_RDBMS')")
public class GroupDAOBatis implements GroupDAO {

}
