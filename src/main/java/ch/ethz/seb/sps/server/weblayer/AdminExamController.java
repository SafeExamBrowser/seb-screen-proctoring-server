package ch.ethz.seb.sps.server.weblayer;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.Page;
import ch.ethz.seb.sps.domain.model.service.ScheduledDelete;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScheduledDeleteRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.dao.ScheduledDeleteDAO;
import io.swagger.v3.core.util.Constants;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.server.servicelayer.*;
import ch.ethz.seb.sps.utils.Result;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.mybatis.dynamic.sql.SqlTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import ch.ethz.seb.sps.domain.Domain.EXAM;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.api.POSTMapper;
import ch.ethz.seb.sps.domain.model.service.Exam;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ExamRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.dao.AuditLogDAO;
import ch.ethz.seb.sps.server.datalayer.dao.ExamDAO;
import ch.ethz.seb.sps.server.datalayer.dao.GroupDAO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("${sps.api.admin.endpoint.v1}" + API.EXAM_ENDPOINT)
public class AdminExamController extends ActivatableEntityController<Exam, Exam> {

    private static final Logger log = LoggerFactory.getLogger(AdminExamController.class);

    private final GroupDAO groupDAO;
    private final SessionService sessionService;
    private final ProctoringService proctoringService;
    private final ScheduledDeleteDAO scheduledDeleteDAO;
    private final ScheduledDeleteService scheduledDeleteService;

    public AdminExamController(
            final UserService userService,
            final ExamDAO entityDAO,
            final AuditLogDAO auditLogDAO,
            final PaginationService paginationService,
            final BeanValidationService beanValidationService,
            final GroupDAO groupDAO,
            final SessionService sessionService,
            final ProctoringService proctoringService,
            final ScheduledDeleteDAO scheduledDeleteDAO,
            final ScheduledDeleteService scheduledDeleteService) {

        super(userService, entityDAO, auditLogDAO, paginationService, beanValidationService);
        this.groupDAO = groupDAO;
        this.sessionService = sessionService;
        this.proctoringService = proctoringService;
        this.scheduledDeleteDAO = scheduledDeleteDAO;
        this.scheduledDeleteService = scheduledDeleteService;

    }

    @Operation(
            summary = "Create a new exam object of specifies type by using the given form parameter",
            description = "This expects " + MediaType.APPLICATION_FORM_URLENCODED_VALUE +
                    " format for the form parameter" +
                    " and tries to create a new entity object from this form parameter, " +
                    "resulting in an error if there are missing" +
                    " or incorrect form parameter. The needed form parameter " +
                    "can be verified within the specific entity object.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = { @Content(
                            schema = @Schema(implementation = Exam.class),
                            mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE) }))
    @Parameter(name = "formParameter", hidden = true)
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public Exam create(
            @RequestParam(required = false) final MultiValueMap<String, String> formParameter,
            final HttpServletRequest request) {

        return super.create(formParameter, request);
    }

    @Operation(
            summary = "Deletes a single entity (and all its dependencies) by its modelId.",
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
            path = API.PARAM_MODEL_PATH_SEGMENT + API.REQUEST_DELETE_ENDPOINT,
            method = RequestMethod.DELETE,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<EntityKey> requestDelete(@PathVariable final String modelId) {
        if (!this.sessionService.hasAnySessionDataForExam(modelId)) {
            return super.hardDelete(modelId);
        } else {
            log.info("Exam will not be deleted because it already has screenshot data assigned to it. Exam: {}", modelId);

            setActiveSingle(modelId, false)
                    .onError(error -> log.error("Failed to close exam on deletion request: {}", error.getMessage()))
                    .onSuccess(exam -> log.info("Closed Exam with data due to deletion request: {}", exam.uuid));

            return Collections.emptyList();
        }
    }

    // ****************************************************************************
    // **** Scheduled Delete

    @RequestMapping(
            path = API.SCHEDULED_DELETE_ENDPOINT,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<ScheduledDelete> getScheduledDeletePage(
            @RequestParam(name = Page.ATTR_PAGE_NUMBER, required = false) final Integer pageNumber,
            @RequestParam(name = Page.ATTR_PAGE_SIZE, required = false) final Integer pageSize,
            @RequestParam(name = Page.ATTR_SORT, required = false) final String sort,
            @RequestParam(required = false) final MultiValueMap<String, String> allRequestParams,
            final HttpServletRequest request) {

        userService.checkIsAdmin();

        final FilterMap filterMap = new FilterMap(allRequestParams, request.getQueryString());
        return this.paginationService.getPage(
                        pageNumber,
                        pageSize,
                        sort,
                        ScheduledDeleteRecordDynamicSqlSupport.scheduledDeleteRecord.tableNameAtRuntime(),
                        () -> scheduledDeleteDAO.allMatching(filterMap))
                .getOrThrow();
    }

    @RequestMapping(
            path = API.SCHEDULED_DELETE_ENDPOINT + API.PARAM_MODEL_PATH_SEGMENT,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ScheduledDelete getScheduledDelete(@PathVariable final String modelId) {

        userService.checkIsAdmin();

        return scheduledDeleteDAO
                .byModelId(modelId)
                .getOrThrow();
    }

    @RequestMapping(
            path = API.SCHEDULED_DELETE_ENDPOINT,
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ScheduledDelete createScheduledDelete(
            @RequestParam(name = Domain.SCHEDULED_DELETE.ATTR_DELETE_DUE_TIME, required = true) final Long dueTimeUTC,
            @RequestParam(name = ScheduledDelete.ATTR_REFERENCE_TIME_ZONE, required = false) final String refTimeZone) {

        userService.checkIsAdmin();

        final DateTimeZone refTZ = refTimeZone != null ? DateTimeZone.forID(refTimeZone) : DateTimeZone.UTC;
        return scheduledDeleteService
                .createScheduledDelete(dueTimeUTC,refTZ)
                .getOrThrow();
    }

    @RequestMapping(
            path = API.MARK_READY_FOR_DELETE,
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<EntityKey> markReadyForDelete(@RequestParam(name = EXAM.ATTR_UUID, required = true) final String examUUIDs) {

        userService.checkIsAdmin();

        String[] split = StringUtils.split(examUUIDs, Constants.COMMA);
        if (split == null) {
            return Collections.emptyList();
        }

        return scheduledDeleteService
                .markExamsReadyForDeletion(Arrays.asList(split))
                .getOrThrow();
    }

    @RequestMapping(
            path = API.EXCLUDE_FROM_DELETE,
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<EntityKey> excludeFromDelete(@RequestParam(name = EXAM.ATTR_UUID, required = true) final String examUUIDs) {

        userService.checkIsAdmin();

        String[] split = StringUtils.split(examUUIDs, Constants.COMMA);
        if (split == null) {
            return Collections.emptyList();
        }

        return scheduledDeleteService
                .excludeExamsFromDeletion(Arrays.asList(split))
                .getOrThrow();

    }

    // **** Scheduled Delete
    // ****************************************************************************

    @Override
    public Collection<EntityKey> hardDelete(String modelId) {
        throw new UnsupportedOperationException("Direct hard delete of Exam is not supported. Use scheduled delete instead");
    }

    @Override
    public Collection<EntityKey> hardDeleteAll(List<String> ids, HttpServletResponse response) {
        throw new UnsupportedOperationException("Direct hard delete of Exam is not supported. Use scheduled delete instead");
    }

    @Override
    protected Exam doBeforeActivation(final Exam entity, final boolean activation) {

        // SEBSP-182 delete all Teacher only entity privileges for the Exam
        if (!activation) {
            this.userService
                    .deleteTeacherPrivileges(entity)
                    .flatMap(exam -> this.proctoringService.updateCacheForExam(entity))
                    .onError(error -> log.error(
                            "Failed to update entity privileges for exam: {}",
                            entity,
                            error));
        }
        
        this.groupDAO
                .applyActivationForAllOfExam(entity.id, activation)
                .map(groupKeys -> {
                    if (!activation) {
                        // if deactivation also deactivate all sessions of groups
                        this.sessionService
                                .closeAllSessions(groupKeys)
                                .getOrThrow();
                    }
                    return groupKeys;
                })
                .onError(
                        error -> log.error(
                                "Failed to apply activation on all groups of exam: {}",
                                entity,
                                error))
                .onSuccess(
                        keys -> log.info(
                                "Successfully apply activation to all groups of exam: {}, {}",
                                entity.uuid,
                                keys));
        return entity;
    }

    @Override
    protected Exam createNew(final POSTMapper postParams) {
        return new Exam(
                null,
                postParams.getString(EXAM.ATTR_UUID),
                postParams.getString(EXAM.ATTR_NAME),
                postParams.getString(EXAM.ATTR_DESCRIPTION),
                postParams.getString(EXAM.ATTR_URL),
                postParams.getString(EXAM.ATTR_TYPE),
                this.userService.getCurrentUserUUIDOrNull(),
                postParams.getStringSet(EXAM.ATTR_SUPPORTER),
                null,
                null,
                null,
                postParams.getLong(EXAM.ATTR_START_TIME),
                postParams.getLong(EXAM.ATTR_END_TIME),
                postParams.getLong(EXAM.ATTR_DELETION_TIME));
    }

    @Override
    protected Result<Exam> notifyCreated(Exam entity) {
        return super
                .notifyCreated(entity)
                .map(this::updateEntityPrivileges);
    }

    @Override
    protected Exam notifySaved(Exam entity) {
        // SEBSP-182 do nothing if exam is not 
        if (entity.isActive()) {
            updateEntityPrivileges(super.notifySaved(entity));
        }
        
        return entity;
    }

    private Exam updateEntityPrivileges(Exam entity) {
        
        this.userService
                .applyExamPrivileges(entity)
                .flatMap(exam -> this.proctoringService.updateCacheForExam(entity))
                .onError(error -> log.error(
                        "Failed to update entity privileges for exam: {}",
                        entity,
                        error));
        
        return entity;
    }

    @Override
    protected Exam merge(final Exam modifyData, final Exam existingEntity) {
        return new Exam(
                existingEntity.id,
                existingEntity.uuid,
                modifyData.name,
                modifyData.description,
                modifyData.url,
                modifyData.type,
                null,
                modifyData.supporter,
                null,
                null,
                null,
                modifyData.startTime,
                modifyData.endTime,
                modifyData.deletionTime);
    }

    @Override
    protected SqlTable getSQLTableOfEntity() {
        return ExamRecordDynamicSqlSupport.examRecord;
    }
}
