package ch.ethz.seb.sps.server.weblayer;

import javax.servlet.http.HttpServletRequest;

import org.mybatis.dynamic.sql.SqlTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.ethz.seb.sps.domain.Domain.EXAM;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.api.POSTMapper;
import ch.ethz.seb.sps.domain.model.service.Exam;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ExamRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.dao.AuditLogDAO;
import ch.ethz.seb.sps.server.datalayer.dao.ExamDAO;
import ch.ethz.seb.sps.server.datalayer.dao.GroupDAO;
import ch.ethz.seb.sps.server.servicelayer.BeanValidationService;
import ch.ethz.seb.sps.server.servicelayer.PaginationService;
import ch.ethz.seb.sps.server.servicelayer.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("${sps.api.admin.endpoint.v1}" + API.EXAM_ENDPOINT)
public class AdminExamController extends ActivatableEntityController<Exam, Exam> {

    private static final Logger log = LoggerFactory.getLogger(AdminExamController.class);

    private final GroupDAO groupDAO;

    public AdminExamController(
            final UserService userService,
            final ExamDAO entityDAO,
            final AuditLogDAO auditLogDAO,
            final PaginationService paginationService,
            final BeanValidationService beanValidationService,
            final GroupDAO groupDAO) {

        super(userService, entityDAO, auditLogDAO, paginationService, beanValidationService);
        this.groupDAO = groupDAO;
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

    @Override
    protected Exam doBeforeActivation(final Exam entity, final boolean activation) {

        this.groupDAO
                .applyActivationForAllOfExam(entity.id, activation)
                .onError(
                        error -> log.error("Failed to apply activation on all groups of exam: {}", entity, error))
                .onSuccess(
                        keys -> log.info("Successfully apply activation to all groups of exam: {}, {}", entity, keys));

        return entity;
    }

    @Override
    protected Exam createNew(final POSTMapper postParams) {
        return new Exam(
                null,
                null,
                postParams.getString(EXAM.ATTR_NAME),
                postParams.getString(EXAM.ATTR_DESCRIPTION),
                postParams.getString(EXAM.ATTR_URL),
                postParams.getString(EXAM.ATTR_TYPE),
                null,
                null,
                null,
                null,
                postParams.getLong(EXAM.ATTR_START_TIME),
                postParams.getLong(EXAM.ATTR_END_TIME),
                null);
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
                null,
                null,
                null,
                modifyData.startTime,
                modifyData.endTime,
                null);
    }

    @Override
    protected SqlTable getSQLTableOfEntity() {
        return ExamRecordDynamicSqlSupport.examRecord;
    }
}
