/*
 * Copyright (c) 2019 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.SqlTable;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.api.API.PrivilegeType;
import ch.ethz.seb.sps.domain.api.APIErrorException;
import ch.ethz.seb.sps.domain.api.POSTMapper;
import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.EntityName;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.Page;
import ch.ethz.seb.sps.server.datalayer.dao.AuditLogDAO;
import ch.ethz.seb.sps.server.datalayer.dao.EntityDAO;
import ch.ethz.seb.sps.server.servicelayer.BeanValidationService;
import ch.ethz.seb.sps.server.servicelayer.PaginationService;
import ch.ethz.seb.sps.server.servicelayer.UserService;
import ch.ethz.seb.sps.utils.Constants;
import ch.ethz.seb.sps.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/** Abstract Entity-Controller that defines generic Entity rest API endpoints that are supported
 * by all entity types.
 *
 * @param <T> The concrete Entity domain-model type used on all GET, PUT
 * @param <M> The concrete Entity domain-model type used for POST methods (new) */
@SecurityRequirement(name = "AdminOAuth")
public abstract class EntityController<T extends Entity, M extends Entity> {

    protected final UserService userService;
    protected final EntityDAO<T, M> entityDAO;
    protected final AuditLogDAO auditLogDAO;
    protected final PaginationService paginationService;
    protected final BeanValidationService beanValidationService;

    protected EntityController(
            final UserService userService,
            final EntityDAO<T, M> entityDAO,
            final AuditLogDAO auditLogDAO,
            final PaginationService paginationService,
            final BeanValidationService beanValidationService) {

        this.userService = userService;
        this.entityDAO = entityDAO;
        this.auditLogDAO = auditLogDAO;
        this.paginationService = paginationService;
        this.beanValidationService = beanValidationService;
    }

    // ******************
    // * GET (getPage)
    // ******************

    /** The generic endpoint to get a Page of domain-entities of a specific type.
     * </p>
     * GET /{api}/{domain-entity-name}
     * </p>
     * For example for the "exam" domain-entity
     * GET /admin-api/v1/exam
     * GET /admin-api/v1/exam?page_number=2&page_size=10&sort=-name
     * GET /admin-api/v1/exam?name=seb&active=true
     * </p>
     * Sorting: the sort parameter to sort the list of entities before paging
     * the sort parameter is the name of the entity-model attribute to sort with a leading '-' sign for
     * descending sort order. Note that not all entity-model attribute are suited for sorting while the most
     * are.
     * </p>
     * Filter: The filter attributes accepted by this API depend on the actual entity model (domain object)
     * and are of the form [domain-attribute-name]=[filter-value]. E.g.: name=abc or type=EXAM. Usually
     * filter attributes of text type are treated as SQL wildcard with %[text]% to filter all text containing
     * a given text-snippet.
     *
     * @param pageNumber the number of the page that is requested
     * @param pageSize the size of the page that is requested
     * @param sort the sort parameter to sort the list of entities before paging
     *            the sort parameter is the name of the entity-model attribute to sort with a leading '-' sign for
     *            descending sort order.
     * @param allRequestParams a MultiValueMap of all request parameter that is used for filtering.
     * @return Page of domain-model-entities of specified type */
    @Operation(
            summary = "Get a page of the specific domain entity. Sorting and filtering is applied before paging",
            description = "Sorting: the sort parameter to sort the list of entities before paging\n"
                    + "the sort parameter is the name of the entity-model attribute to sort with a leading '-' sign for\n"
                    + "descending sort order. Note that not all entity-model attribute are suited for sorting while the most\n"
                    + "are.\n"
                    + "</p>\n"
                    + "Filter: The filter attributes accepted by this API depend on the actual entity model (domain object)\n"
                    + "and are of the form [domain-attribute-name]=[filter-value]. E.g.: name=abc or type=EXAM. Usually\n"
                    + "filter attributes of text type are treated as SQL wildcard with %[text]% to filter all text containing\n"
                    + "a given text-snippet.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = { @Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE) }),
            parameters = {
                    @Parameter(
                            name = Page.ATTR_PAGE_NUMBER,
                            description = "The number of the page to get from the whole list. If the page does not exists, the API retruns with the first page."),
                    @Parameter(
                            name = Page.ATTR_PAGE_SIZE,
                            description = "The size of the page to get."),
                    @Parameter(
                            name = Page.ATTR_SORT,
                            description = "the sort parameter to sort the list of entities before paging"),
                    @Parameter(
                            name = "filterCriteria",
                            description = "Additional filter criterias \n" +
                                    "For OpenAPI 3 input please use the form: {\"columnName\":\"filterValue\"}",
                            example = "{\"name\":\"ethz\"}",
                            required = false,
                            allowEmptyValue = true)
            })
    @RequestMapping(
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<T> getPage(
            @RequestParam(name = Page.ATTR_PAGE_NUMBER, required = false) final Integer pageNumber,
            @RequestParam(name = Page.ATTR_PAGE_SIZE, required = false) final Integer pageSize,
            @RequestParam(name = Page.ATTR_SORT, required = false) final String sort,
            @RequestParam(name = "filterCriteria", required = false) final MultiValueMap<String, String> filterCriteria,
            final HttpServletRequest request) {

        // at least current user must have read access for specified entity type within its own institution
        checkReadPrivilege();

        final FilterMap filterMap = new FilterMap(filterCriteria, request.getQueryString());
        final Page<T> page = this.paginationService.getPage(
                pageNumber,
                pageSize,
                sort,
                getSQLTableOfEntity().name(),
                () -> getAll(filterMap))
                .getOrThrow();

        return page;
    }

    // ******************
    // * GET (names)
    // ******************

    @Operation(
            summary = "Get a filtered list of specific entity name keys.",
            description = "An entity name key is a minimal entity data object with the entity-type, modelId and the name of the entity."
                    + "</p>\n"
                    + "Filter: The filter attributes accepted by this API depend on the actual entity model (domain object)\n"
                    + "and are of the form [domain-attribute-name]=[filter-value]. E.g.: name=abc or type=EXAM. Usually\n"
                    + "filter attributes of text type are treated as SQL wildcard with %[text]% to filter all text containing\n"
                    + "a given text-snippet.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = { @Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE) }),
            parameters = {
                    @Parameter(
                            name = "filterCriteria",
                            description = "Additional filter criteria \n" +
                                    "For OpenAPI 3 input please use the form: {\"columnName\":\"filterValue\"}",
                            example = "{\"name\":\"ethz\"}",
                            required = false,
                            allowEmptyValue = true)
            })
    @RequestMapping(
            path = API.NAMES_PATH_SEGMENT,
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<EntityName> getNames(
            @RequestParam(name = "filterCriteria", required = false) final MultiValueMap<String, String> filterCriteria,
            final HttpServletRequest request) {

        // at least current user must have read access for specified entity type within its own institution
        checkReadPrivilege();

        final FilterMap filterMap = new FilterMap(filterCriteria, request.getQueryString());
        final Collection<T> all = getAll(filterMap)
                .getOrThrow();

        return all
                .stream()
                .map(Entity::toName)
                .collect(Collectors.toList());
    }

    // ******************
    // * GET (single)
    // ******************

    @Operation(
            summary = "Get a single entity by its modelId.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = { @Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE) }),
            parameters = {
                    @Parameter(
                            name = API.PARAM_MODEL_ID,
                            description = "The model identifier of the entity object to get.",
                            in = ParameterIn.PATH)
            })
    @RequestMapping(
            path = API.PARAM_MODEL_PATH_SEGMENT,
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public T getBy(@PathVariable final String modelId) {
        return this.entityDAO
                .byModelId(modelId)
                .flatMap(this::checkReadAccess)
                .getOrThrow();
    }

    // ******************
    // * GET (list)
    // ******************

    @Operation(
            summary = "Get a list of entity objects by a given list of model identifiers of entities.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = { @Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE) }),
            parameters = {
                    @Parameter(
                            name = API.PARAM_MODEL_ID_LIST,
                            description = "Comma separated list of model identifiers.")
            })
    @RequestMapping(
            path = API.LIST_PATH_SEGMENT,
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<T> getForIds(@RequestParam(name = API.PARAM_MODEL_ID_LIST, required = true) final String modelIds) {

        return Result.tryCatch(() -> Arrays.stream(StringUtils.split(modelIds, Constants.LIST_SEPARATOR_CHAR))
                .map(modelId -> new EntityKey(modelId, this.entityDAO.entityType()))
                .collect(Collectors.toSet()))
                .flatMap(this.entityDAO::byEntityKeys)
                .getOrThrow()
                .stream()
                .filter(this::hasReadAccess)
                .collect(Collectors.toList());
    }

    // ******************
    // * POST (create)
    // ******************

    @Operation(
            summary = "Create a new entity object of specifies type by using the given form parameter",
            description = "This expects " + MediaType.APPLICATION_FORM_URLENCODED_VALUE +
                    " format for the form parameter" +
                    " and tries to create a new entity object from this form parameter, " +
                    "resulting in an error if there are missing" +
                    " or incorrect form paramter. The needed form paramter " +

                    "can be verified within the specific entity object.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = { @Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE) }),
            parameters = {
                    @Parameter(
                            name = "formParams",
                            description = "The from paramter value map that is been used to create a new entity object.",
                            in = ParameterIn.DEFAULT)
            })
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public T create(
            @RequestParam final MultiValueMap<String, String> allRequestParams,
            final HttpServletRequest request) {

        // check write privilege for requested institution and concrete entityType
        this.checkWritePrivilege();

        final POSTMapper postMap = new POSTMapper(allRequestParams, request.getQueryString());
        final M requestModel = this.createNew(postMap);
        return this.validForCreate(requestModel)
                .flatMap(this.entityDAO::createNew)
                .flatMap(this::logCreate)
                .flatMap(this::notifyCreated)
                .getOrThrow();
    }

    // ****************
    // * PUT (save)
    // ****************

    @Operation(
            summary = "Modifies an already existing entity object of the specific type.",
            description = "This expects " + MediaType.APPLICATION_JSON_VALUE +
                    " format for the response data and verifies consistencies " +
                    "within the definition of the specific entity object type. " +
                    "Missing (NULL) parameter that are not mandatory will be ignored and the original value will not be affected",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }))
    @RequestMapping(
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public T savePut(@Valid @RequestBody final T modifyData) {
        return this.checkModifyAccess(modifyData)
                .flatMap(this::validForSave)
                .flatMap(this.entityDAO::save)
                .flatMap(this::logModify)
                .flatMap(this::notifySaved)
                .getOrThrow();
    }

    // ************************
    // * DELETE (hard-delete)
    // ************************

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
            path = API.PARAM_MODEL_PATH_SEGMENT,
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<EntityKey> hardDelete(@PathVariable final String modelId) {

        return this.entityDAO.byModelId(modelId)
                .flatMap(this::checkWriteAccess)
                .flatMap(this::validForDelete)
                .flatMap(entity -> this.entityDAO.delete(modelId))
                .flatMap(this::logDeleted)
                .flatMap(this::notifyDeleted)
                .getOrThrow();
    }

    // **************************
    // * DELETE ALL (hard-delete)
    // **************************

    @Operation(
            summary = "Deletes all given entity (and all its dependencies) by a given list of model identifiers.",
            description = "To check or report what dependent object also would be deleted for a certain entity object, "
                    +
                    "please use the dependency endpoint to get a report of all dependend entity objects.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = { @Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE) }),
            parameters = {
                    @Parameter(
                            name = API.PARAM_MODEL_ID_LIST,
                            description = "The list of model identifiers of specific entity type to delete.",
                            in = ParameterIn.QUERY)
            })
    @RequestMapping(
            method = RequestMethod.DELETE,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<EntityKey> hardDeleteAll(
            @RequestParam(name = API.PARAM_MODEL_ID_LIST) final List<String> ids) {

        this.checkWritePrivilege();

        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        final EntityType entityType = this.entityDAO.entityType();
        return this.entityDAO.allOf(ids)
                .map(all -> all.stream()
                        .filter(one -> this.checkWriteAccess(one).hasValue() &&
                                this.validForDelete(one).hasValue())
                        .map(Entity::getModelId)
                        .map(modelId -> new EntityKey(this.entityDAO.modelIDToPK(modelId), entityType))
                        .collect(Collectors.toSet()))
                .flatMap(this.entityDAO::delete)
                .flatMap(this::logDeleted)
                .flatMap(this::notifyDeleted)
                .getOrThrow();
    }

    protected EnumSet<EntityType> convertToEntityType(final boolean addIncludes, final List<String> includes) {
        final EnumSet<EntityType> includeDependencies = (includes != null)
                ? (includes.isEmpty())
                        ? EnumSet.noneOf(EntityType.class)
                        : EnumSet.copyOf(includes.stream().map(name -> {
                            try {
                                return EntityType.valueOf(name);
                            } catch (final Exception e) {
                                return null;
                            }
                        })
                                .filter(Objects::nonNull)
                                .collect(Collectors.toSet()))
                : (addIncludes) ? EnumSet.noneOf(EntityType.class) : null;
        return includeDependencies;
    }

    protected void checkReadPrivilege() {
        this.userService.check(PrivilegeType.READ, getGrantEntityType());
    }

    protected void checkModifyPrivilege() {
        this.userService.check(PrivilegeType.MODIFY, getGrantEntityType());
    }

    protected void checkWritePrivilege() {
        this.userService.check(PrivilegeType.WRITE, getGrantEntityType());
    }

    protected Result<Collection<T>> getAll(final FilterMap filterMap) {
        return this.entityDAO.allMatching(
                filterMap,
                this::hasReadAccess);
    }

    protected Result<T> notifyCreated(final T entity) {
        return Result.of(entity);
    }

    protected Result<M> validForCreate(final M entity) {
        if (entity.getModelId() == null) {
            return this.beanValidationService.validateBean(entity);
        } else {
            return Result.ofError(
                    APIErrorException.entityValidationError(
                            "Model identifier already defined",
                            entity,
                            "validForCreate"));
        }
    }

    protected Result<T> validForSave(final T entity) {
        if (entity.getModelId() != null) {
            return Result.of(entity);
        } else {
            return Result.ofError(
                    APIErrorException.entityValidationError(
                            "Missing model identifier",
                            entity,
                            "validForSave"));
        }
    }

    protected Result<T> validForDelete(final T entity) {
        if (entity.getModelId() != null) {
            return Result.of(entity);
        } else {
            return Result.ofError(
                    APIErrorException.entityValidationError(
                            "Missing model identifier",
                            entity,
                            "validForDelete"));
        }
    }

    protected Result<T> notifySaved(final T entity) {
        return Result.of(entity);
    }

    protected Result<Collection<EntityKey>> notifyDeleted(final Collection<EntityKey> entities) {
        return Result.of(entities);
    }

    protected Result<T> checkReadAccess(final T entity) {
        final Entity grantEntity = toGrantEntity(entity);
        if (grantEntity != null) {
            this.userService.checkRead(grantEntity);
        }
        return Result.of(entity);
    }

    protected boolean hasReadAccess(final T entity) {
        final Entity grantEntity = toGrantEntity(entity);
        if (grantEntity != null) {
            return this.userService.hasReadGrant(grantEntity);
        }

        return true;
    }

    protected Result<T> checkModifyAccess(final T entity) {
        final Entity grantEntity = toGrantEntity(entity);
        if (grantEntity != null) {
            this.userService.checkModify(grantEntity);
        }
        return Result.of(entity);
    }

    protected Result<T> checkWriteAccess(final T entity) {
        final Entity grantEntity = toGrantEntity(entity);
        if (grantEntity != null) {
            this.userService.checkWrite(grantEntity);
        }
        return Result.of(entity);
    }

    /** Gets the Entity instance that holds the grant for a given Entity instance.
     * Usually this this are the same except the case, that a parent entity holds the grant information
     * for a child entity. In this case this method must be overwritten to extract the get the parent entity
     *
     * @param entity the Entity to get the related Entity for granting access privileges
     * @return the Entity instance that holds the grant information for this type of entity */
    protected Entity toGrantEntity(final T entity) {
        return entity;
    }

    /** Get the EntityType of the GrantEntity that is used for grant checks of the concrete Entity.
     *
     * NOTE: override this if the EntityType of the GrantEntity is not the same as the Entity itself.
     * For example, the Exam is the GrantEntity of a Indicator
     *
     * @return the EntityType of the GrantEntity that is used for grant checks of the concrete Entity */
    protected EntityType getGrantEntityType() {
        return this.entityDAO.entityType();
    }

    /** Makes a CREATE user activity log for the specified entity.
     * This may be overwritten if the create user activity log should be skipped.
     *
     * @param entity the Entity instance
     * @return Result of entity */
    protected Result<T> logCreate(final T entity) {
        return this.auditLogDAO.logCreate(entity);
    }

    /** Makes a MODIFY user activity log for the specified entity.
     * This may be overwritten if the create user activity log should be skipped.
     *
     * @param entity the Entity instance
     * @return Result refer to the logged Entity instance or to an error if happened */
    protected Result<T> logModify(final T entity) {
        return this.auditLogDAO.logModify(entity);
    }

    protected Result<Collection<EntityKey>> logDeleted(final Collection<EntityKey> entities) {
        return this.auditLogDAO.logDeleted(entities);
    }

    /** Implements the creation of a new entity from the post parameters given within the POSTMapper
     *
     * @param postParams contains all post parameter from request
     * @return new created Entity instance */
    protected abstract M createNew(POSTMapper postParams);

    /** Gets the MyBatis SqlTable for the concrete Entity
     *
     * @return the MyBatis SqlTable for the concrete Entity */
    protected abstract SqlTable getSQLTableOfEntity();

}