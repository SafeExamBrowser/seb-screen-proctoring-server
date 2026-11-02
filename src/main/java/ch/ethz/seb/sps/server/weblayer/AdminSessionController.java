/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.service.Group;
import ch.ethz.seb.sps.domain.model.service.SessionDeletionInfo;
import ch.ethz.seb.sps.server.datalayer.dao.ExamDAO;
import ch.ethz.seb.sps.server.servicelayer.ScheduledDeleteService;
import ch.ethz.seb.sps.server.servicelayer.impl.ProctoringCacheService;
import ch.ethz.seb.sps.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.servlet.http.HttpServletResponse;
import org.mybatis.dynamic.sql.SqlTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.api.APIErrorException;
import ch.ethz.seb.sps.domain.api.POSTMapper;
import ch.ethz.seb.sps.domain.model.service.Session;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.SessionRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.dao.AuditLogDAO;
import ch.ethz.seb.sps.server.datalayer.dao.GroupDAO;
import ch.ethz.seb.sps.server.datalayer.dao.SessionDAO;
import ch.ethz.seb.sps.server.servicelayer.BeanValidationService;
import ch.ethz.seb.sps.server.servicelayer.PaginationService;
import ch.ethz.seb.sps.server.servicelayer.UserService;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("${sps.api.admin.endpoint.v1}" + API.ADMIN_SESSION_ENDPOINT)
public class AdminSessionController extends ActivatableEntityController<Session, Session> {

    private static final Logger log = LoggerFactory.getLogger(AdminSessionController.class);

    private final GroupDAO groupDAO;
    private final ExamDAO examDAO;
    private final SessionDAO sessionDAO;
    private final ProctoringCacheService proctoringCacheService;
    private final ScheduledDeleteService scheduledDeleteService;

    public AdminSessionController(
            final GroupDAO groupDAO,
            final UserService userService,
            final SessionDAO entityDAO,
            final AuditLogDAO auditLogDAO,
            final PaginationService paginationService,
            final BeanValidationService beanValidationService,
            final ExamDAO examDAO,
            final ProctoringCacheService proctoringCacheService,
            final ScheduledDeleteService scheduledDeleteService) {

        super(userService, entityDAO, auditLogDAO, paginationService, beanValidationService);
        this.groupDAO = groupDAO;
        this.examDAO = examDAO;
        this.sessionDAO = entityDAO;
        this.proctoringCacheService = proctoringCacheService;
        this.scheduledDeleteService = scheduledDeleteService;
    }

    @RequestMapping(
            path = API.SESSION_ENCRYPT_KEY_ENDPOINT + API.PARAM_MODEL_PATH_SEGMENT,
            method = RequestMethod.GET)
    public void getEncryptKey(
            @PathVariable(name = API.PARAM_MODEL_ID) final String sessionUUD,
            final HttpServletResponse response) {

        this.sessionDAO.byModelId(sessionUUD)
                .map(this.userService::checkWrite)
                .flatMap(session -> this.sessionDAO.getEncryptionKey(sessionUUD))
                .onSuccess(key -> {
                    response.setHeader(API.SESSION_HEADER_ENCRYPT_KEY, key);
                    response.setStatus(HttpStatus.OK.value());
                }).onError(error -> {
                    log.error("Failed to get SEB Session encryption key: ", error);
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                });
    }

    @RequestMapping(
            path = API.SESSION_REQUEST_DELETION,
            method = RequestMethod.GET)
    public Collection<SessionDeletionInfo> getSessionDeletionInfo(
            @RequestParam(name = SessionDeletionInfo.ATT_SEARCH_NAME, required = true) final String searchName,
            @RequestParam(name = Domain.SCHEDULED_DELETE.ATTR_DELETE_DUE_TIME, required = false) final Long deleteDueTimeUTC) {

        checkReadPrivilege();
        userService.checkIsAdmin();

        return scheduledDeleteService
                .getSessionDeletionReport(searchName, deleteDueTimeUTC)
                .getOrThrow();
    }

    @Override
    protected Session createNew(final POSTMapper postParams) {
        
        final String uuid = postParams.getUUID(Domain.SESSION.ATTR_UUID, true);
        final String groupId = postParams.getString(Domain.SESSION.ATTR_GROUP_ID);
        final Long groupPK = this.groupDAO.modelIdToPK(groupId);
        
        // check group is active, if not --> error response
        if (groupPK == null) {
            throw APIErrorException.ofMissingAttribute(Domain.SESSION.ATTR_GROUP_ID, groupId);
        }
        Group group = this.groupDAO.byPK(groupPK).getOrThrow();
        if (group.terminationTime != null) {
            throw APIErrorException.ofIllegalState(
                    Domain.SESSION.ATTR_GROUP_ID,
                    "Group closed",
                    groupId);
        }
        // also check if exam is running if the group has an exam
        if (group.exam_id != null) {
            if (!examDAO.isExamRunning(group.exam_id)) {
                throw APIErrorException.ofIllegalState(
                        Domain.SEB_GROUP.ATTR_EXAM_ID,
                        "Exam not running",
                        String.valueOf(group.exam_id));
            }
        }

        return new Session(
                null,
                groupPK,
                uuid,
                postParams.getString(Domain.SESSION.ATTR_CLIENT_NAME),
                postParams.getString(Domain.SESSION.ATTR_CLIENT_IP),
                postParams.getString(Domain.SESSION.ATTR_CLIENT_MACHINE_NAME),
                postParams.getString(Domain.SESSION.ATTR_CLIENT_OS_NAME),
                postParams.getString(Domain.SESSION.ATTR_CLIENT_VERSION),
                postParams.getEnum(Domain.SESSION.ATTR_IMAGE_FORMAT, ImageFormat.class),
                null, null, null);
    }

    @Operation(
            summary = "Securely deletes a single entity (and all its dependencies) by its modelId.",
            description = "To check or report what dependent object also would be deleted for a certain entity object, "
                    +
                    "please use the dependency endpoint to get a report of all dependend entity objects.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = { @Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE) }),
            parameters = {
                    @Parameter(
                            name = API.PARAM_MODEL_ID,
                            description = "The model identifier of the entity object to get.",
                            in = ParameterIn.PATH)
            })
    @RequestMapping(
            path = API.PARAM_MODEL_PATH_SEGMENT + API.SESSION_SECURE_DELETE_ENDPOINT,
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<EntityKey> secureDelete(@PathVariable final String modelId) {

        log.info("Got secure session deletion request for session:  {}", modelId);

        return this.entityDAO.byModelId(modelId)
                .flatMap(this::checkWriteAccess)
                .flatMap(this::validForDelete)
                .flatMap(this::logDelete)
                .flatMap(entity -> this.sessionDAO.secureDeleteSession(modelId))
                .flatMap(key -> notifyDeleted(Collections.singleton(key)))
                .getOrThrow();
    }

    @Override
    protected Result<Session> notifyCreated(Session entity) {
        try {
            proctoringCacheService.evictSessionTokens(groupDAO.byPK(entity.groupId).getOrThrow().uuid);
        } catch (Exception e) {
            log.error("Failed to evict session token cache: ", e);
        }
        return super.notifyCreated(entity);
    }

    @Override
    protected Session merge(final Session modifyData, final Session existingEntity) {
        return new Session(
                existingEntity.id,
                existingEntity.groupId,
                existingEntity.uuid,
                modifyData.clientName,
                modifyData.clientIP,
                modifyData.clientMachineName,
                modifyData.clientOSName,
                modifyData.clientVersion,
                modifyData.imageFormat,
                existingEntity.creationTime,
                null,
                null);
    }

    @Override
    protected SqlTable getSQLTableOfEntity() {
        return SessionRecordDynamicSqlSupport.sessionRecord;
    }

}
