/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import java.util.UUID;

import org.mybatis.dynamic.sql.SqlTable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.api.POSTMapper;
import ch.ethz.seb.sps.domain.model.screenshot.Group;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.GroupRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.dao.AuditLogDAO;
import ch.ethz.seb.sps.server.datalayer.dao.GroupDAO;
import ch.ethz.seb.sps.server.servicelayer.BeanValidationService;
import ch.ethz.seb.sps.server.servicelayer.PaginationService;
import ch.ethz.seb.sps.server.servicelayer.SessionService;
import ch.ethz.seb.sps.server.servicelayer.UserService;

@RestController
@RequestMapping("${sps.api.admin.endpoint.v1}" + API.GROUP_ENDPOINT)
public class AdminGroupController extends ActivatableEntityController<Group, Group> {

    private final SessionService sessionService;

    public AdminGroupController(
            final UserService userService,
            final GroupDAO entityDAO,
            final AuditLogDAO auditLogDAO,
            final PaginationService paginationService,
            final BeanValidationService beanValidationService,
            final SessionService sessionService) {

        super(userService, entityDAO, auditLogDAO, paginationService, beanValidationService);
        this.sessionService = sessionService;
    }

    @RequestMapping(
            path = API.PARAM_MODEL_PATH_SEGMENT + API.SESSION_ENDPOINT,
            method = RequestMethod.GET,
            produces = {
                    MediaType.IMAGE_PNG_VALUE,
                    MediaType.IMAGE_JPEG_VALUE,
                    MediaType.IMAGE_GIF_VALUE,
                    MediaType.APPLICATION_OCTET_STREAM_VALUE })
    public String getActiveSessions(
            @PathVariable(name = API.PARAM_MODEL_ID) final String groupUUID) {

        return this.sessionService
                .getActiveSessions(groupUUID)
                .getOrThrow();
    }

    @Override
    protected Group createNew(final POSTMapper postParams) {
        return new Group(
                null,
                UUID.randomUUID().toString(),
                postParams.getString(Domain.SEB_GROUP.ATTR_NAME),
                postParams.getString(Domain.SEB_GROUP.ATTR_DESCRIPTION),
                null, null, null, null, null);
    }

    @Override
    protected SqlTable getSQLTableOfEntity() {
        return GroupRecordDynamicSqlSupport.groupRecord;
    }

}
