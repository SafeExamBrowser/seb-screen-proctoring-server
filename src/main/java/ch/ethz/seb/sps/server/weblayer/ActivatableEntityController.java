/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import java.util.Collection;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.Activatable;
import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.Page;
import ch.ethz.seb.sps.server.datalayer.dao.ActivatableEntityDAO;
import ch.ethz.seb.sps.server.datalayer.dao.AuditLogDAO;
import ch.ethz.seb.sps.server.servicelayer.BeanValidationService;
import ch.ethz.seb.sps.server.servicelayer.PaginationService;
import ch.ethz.seb.sps.server.servicelayer.UserService;
import ch.ethz.seb.sps.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;

/** Abstract Entity-Controller that defines generic Entity rest API endpoints that are supported
 * by all entity types that has activation feature and can be activated or deactivated.
 *
 * @param <T> The concrete Entity domain-model type used on all GET, PUT
 * @param <M> The concrete Entity domain-model type used for POST methods (new) */
public abstract class ActivatableEntityController<T extends Entity & Activatable, M extends Entity>
        extends EntityController<T, M> {

    public ActivatableEntityController(
            final UserService userService,
            final ActivatableEntityDAO<T, M> entityDAO,
            final AuditLogDAO auditLogDAO,
            final PaginationService paginationService,
            final BeanValidationService beanValidationService) {

        super(userService, entityDAO, auditLogDAO, paginationService, beanValidationService);
    }

    @Operation(
            summary = "Get a page of all specific domain entity that are currently active.",
            description = "Sorting: the sort parameter to sort the list of entities before paging\n"
                    + "the sort parameter is the name of the entity-model attribute to sort with a leading '-' sign for\n"
                    + "descending sort order. Note that not all entity-model attribute are suited for sorting while the most\n"
                    + "are.\n",
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
                            description = "the sort parameter to sort the list of entities before paging")
            })
    @RequestMapping(
            path = API.ACTIVE_PATH_SEGMENT,
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<T> allActive(
            @RequestParam(name = Page.ATTR_PAGE_NUMBER, required = false) final Integer pageNumber,
            @RequestParam(name = Page.ATTR_PAGE_SIZE, required = false) final Integer pageSize,
            @RequestParam(name = Page.ATTR_SORT, required = false) final String sort) {

        final Collection<Long> readPrivilegedPredication = getReadPrivilegedPredication();
        final FilterMap filterMap = new FilterMap()
                .putIfAbsent(Entity.FILTER_ATTR_ACTIVE, "true");

        return this.paginationService.getPage(
                pageNumber,
                pageSize,
                sort,
                getSQLTableOfEntity().tableNameAtRuntime(),
                () -> getAll(filterMap, readPrivilegedPredication)).getOrThrow();
    }

    @Operation(
            summary = "Get a page of all specific domain entity that are currently inactive.",
            description = "Sorting: the sort parameter to sort the list of entities before paging\n"
                    + "the sort parameter is the name of the entity-model attribute to sort with a leading '-' sign for\n"
                    + "descending sort order. Note that not all entity-model attribute are suited for sorting while the most\n"
                    + "are.\n",
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
                            description = "the sort parameter to sort the list of entities before paging")

            })
    @RequestMapping(
            path = API.INACTIVE_PATH_SEGMENT,
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<T> allInactive(
            @RequestParam(name = Page.ATTR_PAGE_NUMBER, required = false) final Integer pageNumber,
            @RequestParam(name = Page.ATTR_PAGE_SIZE, required = false) final Integer pageSize,
            @RequestParam(name = Page.ATTR_SORT, required = false) final String sort) {

        final Collection<Long> readPrivilegedPredication = getReadPrivilegedPredication();
        final FilterMap filterMap = new FilterMap()
                .putIfAbsent(Entity.FILTER_ATTR_ACTIVE, "false");

        return this.paginationService.getPage(
                pageNumber,
                pageSize,
                sort,
                getSQLTableOfEntity().tableNameAtRuntime(),
                () -> getAll(filterMap, readPrivilegedPredication)).getOrThrow();
    }

    @Operation(
            summary = "Activate a single entity by its modelId.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = { @Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE) }),
            parameters = {
                    @Parameter(
                            name = API.PARAM_MODEL_ID,
                            description = "The model identifier of the entity object to activate.",
                            in = ParameterIn.PATH)
            })
    @RequestMapping(
            path = API.PATH_VAR_ACTIVE,
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public T activate(@PathVariable final String modelId) {
        return setActiveSingle(modelId, true)
                .getOrThrow();
    }

    @Operation(
            summary = "Deactivate a single entity by its modelId.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = { @Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE) }),
            parameters = {
                    @Parameter(
                            name = API.PARAM_MODEL_ID,
                            description = "The model identifier of the entity object to deactivate.",
                            in = ParameterIn.PATH)
            })
    @RequestMapping(
            value = API.PATH_VAR_INACTIVE,
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public T deactivate(@PathVariable final String modelId) {
        return setActiveSingle(modelId, false)
                .getOrThrow();
    }

    private Result<T> setActiveSingle(final String modelId, final boolean active) {
        return this.entityDAO
                .byModelId(modelId)
                .map(this.userService::checkWrite)
                .map(entity -> validForActivation(entity, active))
                .map(entity -> doBeforeActivation(entity, active))
                .map(entity -> doActivation(entity, active))
                .map(entity -> doAfterActivation(entity, active))
                .map(this::notifySaved);
    }

    protected T validForActivation(final T entity, final boolean activation) {
        if ((entity.isActive() && !activation) || (!entity.isActive() && activation)) {
            return entity;
        } else {
            throw new ActivationMismatchException("Activation argument mismatch. Element is already " + (activation ? "active" : "inactive"));
        }
    }

    protected T doBeforeActivation(final T entity, final boolean activation) {
        return entity;
    }

    protected T doAfterActivation(final T entity, final boolean activation) {
        return entity;
    }

    protected T doActivation(final T entity, final boolean activation) {
        return ((ActivatableEntityDAO<T, M>) this.entityDAO)
                .setActive(entity, activation)
                .getOrThrow();
    }

}
