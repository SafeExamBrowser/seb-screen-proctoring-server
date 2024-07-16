/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import ch.ethz.seb.sps.domain.model.service.GroupSessionCount;
import org.mybatis.dynamic.sql.SqlTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.api.POSTMapper;
import ch.ethz.seb.sps.domain.model.service.Group;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.GroupRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.dao.AuditLogDAO;
import ch.ethz.seb.sps.server.datalayer.dao.ExamDAO;
import ch.ethz.seb.sps.server.datalayer.dao.GroupDAO;
import ch.ethz.seb.sps.server.datalayer.dao.SessionDAO;
import ch.ethz.seb.sps.server.servicelayer.BeanValidationService;
import ch.ethz.seb.sps.server.servicelayer.GroupService;
import ch.ethz.seb.sps.server.servicelayer.PaginationService;
import ch.ethz.seb.sps.server.servicelayer.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("${sps.api.admin.endpoint.v1}" + API.GROUP_ENDPOINT)
public class AdminGroupController extends ActivatableEntityController<Group, Group> {

    private static final Logger log = LoggerFactory.getLogger(AdminGroupController.class);

    private final SessionDAO sessionDAO;
    private final ExamDAO examDAO;
    private final GroupService groupService;

    public AdminGroupController(
            final UserService userService,
            final GroupDAO entityDAO,
            final SessionDAO sessionDA,
            final ExamDAO examDAO,
            final GroupService groupService,
            final AuditLogDAO auditLogDAO,
            final PaginationService paginationService,
            final BeanValidationService beanValidationService) {

        super(userService, entityDAO, auditLogDAO, paginationService, beanValidationService);
        this.sessionDAO = sessionDA;
        this.examDAO = examDAO;
        this.groupService = groupService;
    }

    @Operation(
            summary = "Create a new group object of specifies type by using the given form parameter",
            description = "This expects " + MediaType.APPLICATION_FORM_URLENCODED_VALUE +
                    " format for the form parameter" +
                    " and tries to create a new entity object from this form parameter, " +
                    "resulting in an error if there are missing" +
                    " or incorrect form parameter. The needed form parameter " +
                    "can be verified within the specific entity object.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = { @Content(
                            schema = @Schema(implementation = Group.class),
                            mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE) }))
    @Parameter(name = "formParameter", hidden = true)
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public Group create(
            @RequestParam(required = false) final MultiValueMap<String, String> formParameter,
            final HttpServletRequest request) {

        return super.create(formParameter, request);
    }

    @Override
    protected Group doBeforeActivation(final Group entity, final boolean activation) {

        // apply only deactivation on all depending on sessions
        if (activation) {
            return entity;
        }

        this.sessionDAO
                .closeAllSessionsForGroup(entity.id)
                .onError(
                        error -> log.error(
                                "Failed to apply close on all sessions of group: {}",
                                entity,
                                error))
                .onSuccess(
                        keys -> log.info(
                                "Successfully apply close to all sessions of group: {}, {}",
                                entity,
                                keys));
        return entity;
    }

    @Override
    protected Group createNew(final POSTMapper postParams) {

        final String examModelId = postParams.getString(Domain.SEB_GROUP.ATTR_EXAM_ID);
        final Long entityId = (examModelId != null) ? this.examDAO.modelIdToPK(examModelId) : null;

        return new Group(
                null,
                null,
                postParams.getString(Domain.SEB_GROUP.ATTR_NAME),
                postParams.getString(Domain.SEB_GROUP.ATTR_DESCRIPTION),
                this.userService.getCurrentUserUUIDOrNull(),
                null,
                null,
                null,
                entityId,
                null);
    }

    @Override
    protected Group merge(final Group modifyData, final Group existingEntity) {
        return new Group(
                existingEntity.id,
                existingEntity.uuid,
                modifyData.name,
                modifyData.description,
                null,
                null,
                null,
                null,
                modifyData.exam_id,
                null);
    }

    @Override
    protected SqlTable getSQLTableOfEntity() {
        return GroupRecordDynamicSqlSupport.groupRecord;
    }

    @Override
    protected Collection<Long> getReadPrivilegedPredication() {
        return this.groupService.getReadPrivilegedPredication();
    }

}
