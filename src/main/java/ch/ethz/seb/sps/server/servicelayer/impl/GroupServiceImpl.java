/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.server.datalayer.dao.ExamDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import ch.ethz.seb.sps.domain.api.API.PrivilegeType;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.server.datalayer.dao.GroupDAO;
import ch.ethz.seb.sps.server.servicelayer.GroupService;
import ch.ethz.seb.sps.server.servicelayer.UserService;

@Lazy
@Service
public class GroupServiceImpl implements GroupService {

    private static final Logger log = LoggerFactory.getLogger(GroupServiceImpl.class);

    private final UserService userService;
    private final ExamDAO examDAO;
    private final GroupDAO groupDAO;

    public GroupServiceImpl(
            final UserService userService,
            final ExamDAO examDAO,
            final GroupDAO groupDAO) {

        this.userService = userService;
        this.examDAO = examDAO;
        this.groupDAO = groupDAO;
    }

    @Override
    public Collection<Long> getReadPrivilegedPredication() {
        if (this.userService.hasGrant(PrivilegeType.READ, EntityType.SEB_GROUP)) {
            return Collections.emptyList();
        }

        // TODO check if current user only has READ_ONLY_ACTIVE privilege. If so filter also termination date == null

        // list of group id's with users entity read privileges
        final Set<Long> directGrants = this.userService
                .getIdsWithReadEntityPrivilege(EntityType.SEB_GROUP)
                .getOrThrow();

        // list of exam id's with users entity read privileges
        final Set<Long> examGrants = new HashSet<>();
        if (userService.hasRole(API.UserRole.INST_ADMIN)) {
            // in this case the user has access to all Exams within the same institution
            try {
                final Long instId = userService.getCurrentUser().getUserInfo().institutionId;
                examGrants.addAll(
                        this.examDAO.getAllExamIdsOfInstitution(instId)
                );
            } catch (Exception e) {
                log.error("Failed to get institution of logged in user: {}", e.getMessage());
                examGrants.addAll(
                        this.userService
                        .getIdsWithReadEntityPrivilege(EntityType.EXAM)
                        .getOr(Collections.emptySet())
                );
            }
        } else {
            examGrants.addAll(this.userService
                    .getIdsWithReadEntityPrivilege(EntityType.EXAM)
                    .getOr(Collections.emptySet()));
        }

        final Set<Long> privileged = new HashSet<>((examGrants.isEmpty())
                ? directGrants
                : this.groupDAO
                        .allIdsForExamsIds(examGrants)
                        .map(grants -> {
                            final Set<Long> result = new HashSet<>(grants);
                            result.addAll(directGrants);
                            return result;
                        })
                        .onError(error -> {
                            log.error("Failed to get Exam based grants for groups: ", error);
                        })
                        .getOr(directGrants));

        // no privileges at all, return never matching PK in list
        if (privileged.isEmpty()) {
            privileged.add(-1L);
        }
        return privileged;
    }

}
