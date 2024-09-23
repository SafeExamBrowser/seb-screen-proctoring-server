/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import java.sql.Date;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import ch.ethz.seb.sps.domain.model.service.*;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDataDAO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.Page;
import ch.ethz.seb.sps.domain.model.PageSortOrder;
import ch.ethz.seb.sps.server.ServiceConfig;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.SessionRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.dao.GroupDAO;
import ch.ethz.seb.sps.server.datalayer.dao.NoResourceFoundException;
import ch.ethz.seb.sps.server.servicelayer.GroupService;
import ch.ethz.seb.sps.server.servicelayer.GroupingService;
import ch.ethz.seb.sps.server.servicelayer.PaginationService;
import ch.ethz.seb.sps.server.servicelayer.ProctoringService;
import ch.ethz.seb.sps.utils.Constants;
import ch.ethz.seb.sps.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import ch.ethz.seb.sps.utils.Utils;

@RestController
@RequestMapping("${sps.api.admin.endpoint.v1}" + API.PROCTORING_ENDPOINT)
@SecurityRequirement(name = WebConfig.SWAGGER_AUTH_ADMIN_API)
public class AdminProctorController {

    private static final Logger log = LoggerFactory.getLogger(AdminProctorController.class);

    private final Executor downloadExecutor;
    private final GroupDAO groupDAO;
    private final ScreenshotDataDAO screenshotDataDAO;
    private final GroupService groupService;
    private final ProctoringService proctoringService;
    private final PaginationService paginationService;
    private final GroupingService groupingService;

    public AdminProctorController(
            final GroupDAO groupDAO,
            final ScreenshotDataDAO screenshotDataDAO,
            final GroupService groupService,
            final ProctoringService proctoringService,
            final PaginationService paginationService,
            final GroupingService groupingService,
            @Qualifier(value = ServiceConfig.SCREENSHOT_DOWNLOAD_API_EXECUTOR) final Executor downloadExecutor) {

        this.downloadExecutor = downloadExecutor;
        this.groupDAO = groupDAO;
        this.screenshotDataDAO = screenshotDataDAO;
        this.groupService = groupService;
        this.paginationService = paginationService;
        this.proctoringService = proctoringService;
        this.groupingService = groupingService;
    }

    @RequestMapping(
            path = API.ACTIVE_COUNTS_ENDPOINT,
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<GroupSessionCount> getActivateGroupSessionCounts() {
        return this.proctoringService
                .getActivateGroupSessionCounts()
                .getOrThrow();

    }

    @Operation(
            summary = "Get a page of all groups the requesting user can access for proctoring",
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
                            name = API.PARAM_INCLUDE_PAST_EXAMS,
                            description = "set to true to include past exams",
                            in = ParameterIn.QUERY),
                    @Parameter(
                            name = API.PARAM_INCLUDE_UPCOMING_EXAMS,
                            description = "set to true to include upcoming exams",
                            in = ParameterIn.QUERY),
            })
    @RequestMapping(
            path = API.GROUP_ENDPOINT,
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<GroupViewData> getGroups(
            @RequestParam(name = Page.ATTR_PAGE_NUMBER, required = false) final Integer pageNumber,
            @RequestParam(name = Page.ATTR_PAGE_SIZE, required = false) final Integer pageSize,
            @RequestParam(name = Page.ATTR_SORT, required = false) final String sort,
            @RequestParam(name = API.PARAM_INCLUDE_PAST_EXAMS, required = false) final Boolean includePastExams,
            @RequestParam(name = API.PARAM_INCLUDE_UPCOMING_EXAMS, required = false) final Boolean includeUpcomingExams,
            final HttpServletRequest request) {

        final FilterMap filterMap = new FilterMap(request);

        final Collection<Long> readPrivilegedPredication = this.groupService.getReadPrivilegedPredication();
        return this.paginationService.getPageOf(
                pageNumber,
                pageSize,
                sort,
                ScreenshotDataRecordDynamicSqlSupport.screenshotDataRecord.tableNameAtRuntime(),
                () -> this.groupDAO.getGroupsWithExamData(filterMap, readPrivilegedPredication))
                .getOrThrow();
    }

    @Operation(
            summary = "Get a page of screen proctoring session data of a given group",
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
                            name = API.PARAM_GROUP_ID,
                            description = "The UUID of the group to get a page of session for"),
                    @Parameter(
                            name = ScreenshotsInGroupData.ATTR_PAGE_NUMBER,
                            description = "The number of the page to get from the whole list. If the page does not exists, the API retruns with the first page."),
                    @Parameter(
                            name = ScreenshotsInGroupData.ATTR_PAGE_SIZE,
                            description = "The size of the page to get."),
                    @Parameter(
                            name = ScreenshotsInGroupData.ATTR_SORT_BY,
                            description = "the sort parameter to sort the list of entities before paging"),
                    @Parameter(
                            name = ScreenshotsInGroupData.ATTR_SORT_ORDER,
                            description = "The sorting order"),
                    @Parameter(
                            name = "filterCriteria",
                            description = "Additional search filter criteria \n" +
                                    "This is a collecting map of all request parameter and used by the method to extract "
                                    +
                                    "known search filter criteria and if available in the mapping use it for the search request\n"
                                    +
                                    "NOTE: For OpenAPI 3 input please use the form: {\"columnName\":\"filterValue\"}",
                            example = "{\"active\":true}",
                            required = false,
                            allowEmptyValue = true)
            })
    @RequestMapping(
            path = API.GROUP_ENDPOINT + API.GROUP_ID_PATH_SEGMENT,
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ScreenshotsInGroupData getSessionsByGroup(
            @PathVariable(name = API.PARAM_GROUP_ID) final String groupUUID,
            @RequestParam(name = ScreenshotsInGroupData.ATTR_PAGE_NUMBER, required = false) final Integer pageNumber,
            @RequestParam(name = ScreenshotsInGroupData.ATTR_PAGE_SIZE, required = false) final Integer pageSize,
            @RequestParam(name = ScreenshotsInGroupData.ATTR_SORT_BY, required = false) final String sortBy,
            @RequestParam(name = ScreenshotsInGroupData.ATTR_SORT_ORDER, required = false) final PageSortOrder sortOrder,
            @RequestParam(name = "filterCriteria", required = false) final MultiValueMap<String, String> filterCriteria,
            final HttpServletRequest request) {

        this.proctoringService.checkMonitoringAccess(groupUUID);

        final FilterMap filterMap = new FilterMap(filterCriteria, request.getQueryString());
        return this.proctoringService
                .getSessionsByGroup(groupUUID, pageNumber, pageSize, sortBy, sortOrder, filterMap)
                .getOrThrow();
    }

    @Operation(
            summary = "Get the latest screenshot view and meta data for a given session",
            description = "TODO",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = { @Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE) }),
            parameters = {
                    @Parameter(
                            name = API.PARAM_SESSION_ID,
                            description = "The UUID of the session to get the latest screenshot for")
            })
    @RequestMapping(
            path = API.SCREENSHOT_DATA_ENDPOINT + API.SESSION_ID_PATH_SEGMENT,
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ScreenshotViewData getScreenshotViewData(
            @PathVariable(name = API.PARAM_SESSION_ID, required = true) final String sessionUUID) {

        return getScreenshotViewData(sessionUUID, null);
    }

    @Operation(
            summary = "Get the recorded screenshot view and meta data for a given session at a specified time",
            description = "If there is no existing screenshot at the given point in time or the time before, " +
                    "this method checks if there is any existing screenshot in the future and if so, this will " +
                    "return the data for that screenshot. If there is no screenshot for the given session yet " +
                    "this will respond with a an respecting error response",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = { @Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE) }),
            parameters = {
                    @Parameter(
                            name = API.PARAM_SESSION_ID,
                            description = "The UUID of the session to get the latest screenshot for"),
                    @Parameter(
                            name = API.PARAM_TIMESTAMP,
                            description = "The unix time-stamp (UTC) in milliseconds")
            })
    @RequestMapping(
            path = API.SCREENSHOT_DATA_ENDPOINT + API.SESSION_ID_TIMESTAMP_PATH_SEGMENT,
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ScreenshotViewData getScreenshotViewData(
            @PathVariable(name = API.PARAM_SESSION_ID, required = true) final String sessionUUID,
            @PathVariable(name = API.PARAM_TIMESTAMP, required = true) final String timestamp) {

        this.proctoringService.checkMonitoringSessionAccess(sessionUUID);

        Long ts = null;
        if (StringUtils.isNotBlank(timestamp)) {
            try {
                ts = Long.valueOf(timestamp);
            } catch (final Exception e) {
                throw new BadRequestException("getScreenshotViewData", "Failed to parse timestamp: " + timestamp);
            }
        }

        return this.proctoringService
                .getRecordedImageDataAt(sessionUUID, ts)
                .getOrThrow();
    }

    @Operation(
            summary = "Get the latest screenshot image data for a given session",
            description = "TODO",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = { @Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE) }),
            responses = @ApiResponse(
                    description = "This response with binary image data",
                    content = { @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE) }),
            parameters = {
                    @Parameter(
                            name = API.PARAM_SESSION_ID,
                            description = "The UUID of the session to get the latest screenshot for")
            })
    @RequestMapping(
            path = API.SCREENSHOT_ENDPOINT + API.SESSION_ID_PATH_SEGMENT,
            method = RequestMethod.GET,
            produces = {
                    MediaType.IMAGE_PNG_VALUE,
                    Constants.MIME_TYPE_IMAGE_WEBP,
                    MediaType.APPLICATION_OCTET_STREAM_VALUE })
    public CompletableFuture<Void> getLatestScreenshot(
            @PathVariable(name = API.PARAM_SESSION_ID, required = true) final String sessionUUID,
            final HttpServletResponse response) {

        return getScreenshot(sessionUUID, null, response);
    }

    @Operation(
            summary = "Get the latest screenshot image data for a given session",
            description = "TODO",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = { @Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE) }),
            responses = @ApiResponse(
                    description = "This response with binary image data",
                    content = { @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE) }),
            parameters = {
                    @Parameter(
                            name = API.PARAM_SESSION_ID,
                            description = "The UUID of the session to get the latest screenshot for"),
                    @Parameter(
                            name = API.PARAM_TIMESTAMP,
                            description = "The unix time-stamp (UTC) in milliseconds")
            })
    @RequestMapping(
            path = API.SCREENSHOT_ENDPOINT + API.SESSION_ID_TIMESTAMP_PATH_SEGMENT,
            method = RequestMethod.GET,
            produces = {
                    MediaType.IMAGE_PNG_VALUE,
                    Constants.MIME_TYPE_IMAGE_WEBP,
                    MediaType.APPLICATION_OCTET_STREAM_VALUE })
    public CompletableFuture<Void> getScreenshot(
            @PathVariable(name = API.PARAM_SESSION_ID, required = true) final String sessionUUID,
            @PathVariable(name = API.PARAM_TIMESTAMP, required = false) final String timestamp,
            final HttpServletResponse response) {

        //this.userService.check(PrivilegeType.READ, EntityType.SESSION);
        this.proctoringService.checkMonitoringSessionAccess(sessionUUID);

        return CompletableFuture.runAsync(
                () -> {

                    try {

                        this.proctoringService.streamScreenshot(
                                sessionUUID,
                                (StringUtils.isNotBlank(timestamp)) ? Long.parseLong(timestamp) : null,
                                response::setContentType,
                                response.getOutputStream());

                        response.setStatus(HttpStatus.OK.value());

                    } catch (final NumberFormatException nfe) {
                        log.error("Failed to parse timestamp: {}", timestamp);
                        response.setStatus(HttpStatus.BAD_REQUEST.value());
                    } catch (final NoResourceFoundException nre) {
                        log.error("Failed to stream image file: {}", nre.getMessage());
                        response.setStatus(HttpStatus.NOT_FOUND.value());
                    } catch (final Exception e) {
                        log.error("Failed to stream image file: {}", e.getMessage());
                        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    }
                },
                this.downloadExecutor);
    }

    @Operation(
            summary = "Get the requested page of a given screenshot search result",
            description = "The search query includes specific and generic filter criteria and paging as well as sorting. See detailed description for each part in the parameter description",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = { @Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE) }),
            parameters = {
                    @Parameter(
                            name = API.PARAM_GROUP_ID,
                            description = "The group UUID filter criteria. If available the search is restricted to the given group. The value must be the UUID or the PK of the group",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.PARAM_GROUP_NAME,
                            description = "The group name filter criteria. If available the search is restricted to the given full text search on group name",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.PARAM_SESSION_ID,
                            description = "The session filter criteria. If available the search is restricted to the given session. The value must be the UUID or the PK of the session",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.PARAM_FROM_TIME,
                            description = "The search from-time filter criteria. If given only matches from this time onwards are part of the search result. Value must be a unix timestamp in millisecods in UTC timezone",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.PARAM_TO_TIME,
                            description = "The search to-time filter criteria. If given only matches from this time backwards in time are part of the search result. Value must be a unix timestamp in millisecods in UTC timezone",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = Domain.SESSION.ATTR_CLIENT_NAME,
                            description = "The search filter criteria for a specific session user name. This is used for full-text search on the participant/students login-name session-field",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = Domain.SESSION.ATTR_CLIENT_MACHINE_NAME,
                            description = "The search filter criteria for a specific session user machine name. This is used for full-text search on the participant/students machine name session-field",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = Domain.SESSION.ATTR_CLIENT_OS_NAME,
                            description = "The search filter criteria for a specific session user machine operating system name. This is used for full-text search on the participant/students machine operating system name session-field",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = Domain.SESSION.ATTR_CLIENT_VERSION,
                            description = "The search filter criteria for a specific session user SEB version. This is used for full-text search on the participant/students SEB version session-field",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.SCREENSHOT_META_DATA_APPLICATION,
                            description = "The search filter criteria for screenshot application metadata. This is used for full-text search in screenshot meta data",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.SCREENSHOT_META_DATA_BROWSER_TITLE,
                            description = "The search filter criteria for browser title metadata. This is used for full-text search in screenshot meta data",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.SCREENSHOT_META_DATA_BROWSER_URL,
                            description = "The search filter criteria for screenshot browser URL metadata. This is used for full-text search in screenshot meta data",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.SCREENSHOT_META_DATA_ACTIVE_WINDOW_TITLE,
                            description = "The search filter criteria for screenshot active window title metadata. This is used for full-text search in screenshot meta data",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.SCREENSHOT_META_DATA_USER_ACTION,
                            description = "The search filter criteria for screenshot user action metadata. This is used for full-text search in screenshot meta data",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = Page.ATTR_PAGE_NUMBER,
                            description = "The number of the page to get from the whole list. If the page does not exists, the API returns with the first page.",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = Page.ATTR_PAGE_SIZE,
                            description = "The size of the page to get. Default is 10",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = Page.ATTR_SORT,
                            in = ParameterIn.QUERY,
                            description = "The sort parameter to sort the result list of entities before paging. Sorting is only possible for: imageTimestamp, sessionStartTime, sessionEndTime, sessionClientName, sessionClientIP, sessionClientMachineName, sessionClientOSName, sessionClientVersion. Use a leading '-' sign for descending sort order.",
                            required = false)
            })
    @RequestMapping(
            path = API.SCREENSHOT_SEARCH_ENDPOINT,
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<ScreenshotSearchResult> searchScreenshots(
            @RequestParam(name = API.PARAM_GROUP_ID, required = false) final String groupUUID,
            @RequestParam(name = API.PARAM_GROUP_NAME, required = false) final String groupName,
            @RequestParam(name = API.PARAM_SESSION_ID, required = false) final String sessionUUID,
            @RequestParam(name = API.PARAM_FROM_TIME, required = false) final Long fromTime,
            @RequestParam(name = API.PARAM_TO_TIME, required = false) final Long toTime,
            @RequestParam(name = Page.ATTR_PAGE_NUMBER, required = false) final Integer pageNumber,
            @RequestParam(name = Page.ATTR_PAGE_SIZE, required = false) final Integer pageSize,
            @RequestParam(name = Page.ATTR_SORT, required = false) final String sortBy,
            final HttpServletRequest request) {

        final FilterMap filterMap = new FilterMap(request);

        return this.paginationService.getPageOf(
                pageNumber,
                pageSize,
                sortBy,
                ScreenshotDataRecordDynamicSqlSupport.screenshotDataRecord.tableNameAtRuntime(),
                () -> preProcessGroupCriteria(filterMap),
                () -> queryScreenshots(filterMap))
                .getOrThrow();
    }

    @Operation(
            summary = "Returns a list of of dates where at least one session matches the given search criteria.",
            description = "This search takes all available data into account and checks if min. 1 session a day for the given timeframe matches search criteria.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = { @Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE) }),
            parameters = {
                    @Parameter(
                            name = API.PARAM_GROUP_ID,
                            description = "The group UUID filter criteria. If available the search is restricted to the given group. The value must be the UUID or the PK of the group",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.PARAM_EXAM_NAME,
                            description = "The exam name filter criteria. If available the search is restricted to the given full text search on exam name",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.PARAM_GROUP_NAME,
                            description = "The group name filter criteria. If available the search is restricted to the given full text search on group name",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.PARAM_SESSION_ID,
                            description = "The session filter criteria. If available the search is restricted to the given session. The value must be the UUID or the PK of the session",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.PARAM_FROM_TIME,
                            description = "The search from-time filter criteria. If given only matches from this time onwards are part of the search result. Value must be a unix timestamp in millisecods in UTC timezone",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.PARAM_TO_TIME,
                            description = "The search to-time filter criteria. If given only matches from this time backwards in time are part of the search result. Value must be a unix timestamp in millisecods in UTC timezone",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = Domain.SESSION.ATTR_CLIENT_NAME,
                            description = "The search filter criteria for a specific session user name. This is used for full-text search on the participant/students login-name session-field",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = Domain.SESSION.ATTR_CLIENT_MACHINE_NAME,
                            description = "The search filter criteria for a specific session user machine name. This is used for full-text search on the participant/students machine name session-field",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = Domain.SESSION.ATTR_CLIENT_OS_NAME,
                            description = "The search filter criteria for a specific session user machine operating system name. This is used for full-text search on the participant/students machine operating system name session-field",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = Domain.SESSION.ATTR_CLIENT_VERSION,
                            description = "The search filter criteria for a specific session user SEB version. This is used for full-text search on the participant/students SEB version session-field",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.SCREENSHOT_META_DATA_APPLICATION,
                            description = "The search filter criteria for screenshot application metadata. This is used for full-text search in screenshot meta data",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.SCREENSHOT_META_DATA_BROWSER_TITLE,
                            description = "The search filter criteria for browser title metadata. This is used for full-text search in screenshot meta data",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.SCREENSHOT_META_DATA_BROWSER_URL,
                            description = "The search filter criteria for screenshot browser URL metadata. This is used for full-text search in screenshot meta data",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.SCREENSHOT_META_DATA_ACTIVE_WINDOW_TITLE,
                            description = "The search filter criteria for screenshot active window title metadata. This is used for full-text search in screenshot meta data",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.SCREENSHOT_META_DATA_USER_ACTION,
                            description = "The search filter criteria for screenshot user action metadata. This is used for full-text search in screenshot meta data",
                            in = ParameterIn.QUERY,
                            required = false)})
    @RequestMapping(
            path = API.SESSION_DAY_SEARCH_ENDPOINT,
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Date> getMatchingDaysForSessionSearch(
            @RequestParam(name = API.PARAM_EXAM_NAME, required = false) final String examName,
            @RequestParam(name = API.PARAM_GROUP_ID, required = false) final String groupUUID,
            @RequestParam(name = API.PARAM_GROUP_NAME, required = false) final String groupName,
            @RequestParam(name = API.PARAM_SESSION_ID, required = false) final String sessionUUID,
            @RequestParam(name = API.PARAM_FROM_TIME, required = false) final Long fromTime,
            @RequestParam(name = API.PARAM_TO_TIME, required = false) final Long toTime,
            final HttpServletRequest request) {

        final FilterMap filterMap = new FilterMap(request);

        preProcessGroupCriteria(filterMap);

        return queryMatchingDaysForSessionSearch(filterMap)
                .getOrThrow();
    }

    @Operation(
            summary = "Get the requested page of a given session search result",
            description = "This search is an extension to the generic session search to apply more screenshot specific data as a result. The search query includes specific and generic filter criteria and paging as well as sorting. See detailed description for each part in the parameter description",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = { @Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE) }),
            parameters = {
                    @Parameter(
                            name = API.PARAM_GROUP_ID,
                            description = "The group UUID filter criteria. If available the search is restricted to the given group. The value must be the UUID or the PK of the group",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.PARAM_EXAM_NAME,
                            description = "The exam name filter criteria. If available the search is restricted to the given full text search on exam name",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.PARAM_GROUP_NAME,
                            description = "The group name filter criteria. If available the search is restricted to the given full text search on group name",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.PARAM_SESSION_ID,
                            description = "The session filter criteria. If available the search is restricted to the given session. The value must be the UUID or the PK of the session",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.PARAM_FROM_TIME,
                            description = "The search from-time filter criteria. If given only matches from this time onwards are part of the search result. Value must be a unix timestamp in millisecods in UTC timezone",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.PARAM_TO_TIME,
                            description = "The search to-time filter criteria. If given only matches from this time backwards in time are part of the search result. Value must be a unix timestamp in millisecods in UTC timezone",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = Domain.SESSION.ATTR_CLIENT_NAME,
                            description = "The search filter criteria for a specific session user name. This is used for full-text search on the participant/students login-name session-field",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = Domain.SESSION.ATTR_CLIENT_MACHINE_NAME,
                            description = "The search filter criteria for a specific session user machine name. This is used for full-text search on the participant/students machine name session-field",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = Domain.SESSION.ATTR_CLIENT_OS_NAME,
                            description = "The search filter criteria for a specific session user machine operating system name. This is used for full-text search on the participant/students machine operating system name session-field",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = Domain.SESSION.ATTR_CLIENT_VERSION,
                            description = "The search filter criteria for a specific session user SEB version. This is used for full-text search on the participant/students SEB version session-field",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.SCREENSHOT_META_DATA_APPLICATION,
                            description = "The search filter criteria for screenshot application metadata. This is used for full-text search in screenshot meta data",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.SCREENSHOT_META_DATA_BROWSER_TITLE,
                            description = "The search filter criteria for browser title metadata. This is used for full-text search in screenshot meta data",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.SCREENSHOT_META_DATA_BROWSER_URL,
                            description = "The search filter criteria for screenshot browser URL metadata. This is used for full-text search in screenshot meta data",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.SCREENSHOT_META_DATA_ACTIVE_WINDOW_TITLE,
                            description = "The search filter criteria for screenshot active window title metadata. This is used for full-text search in screenshot meta data",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.SCREENSHOT_META_DATA_USER_ACTION,
                            description = "The search filter criteria for screenshot user action metadata. This is used for full-text search in screenshot meta data",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = Page.ATTR_PAGE_NUMBER,
                            description = "The number of the page to get from the whole list. If the page does not exists, the API returns with the first page.",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = Page.ATTR_PAGE_SIZE,
                            description = "The size of the page to get. Default is 10",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = Page.ATTR_SORT,
                            in = ParameterIn.QUERY,
                            description = "The sort parameter to sort the result list of entities before paging. Sorting is only possible for: startTime, imageFormat, clientName, clientIp, clientOsName, clientVersion, clientMachineName. Use a leading '-' sign for descending sort order.",
                            required = false)
            })
    @RequestMapping(
            path = API.SESSION_SEARCH_ENDPOINT,
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SessionSearchResult> searchSessions(
            @RequestParam(name = API.PARAM_EXAM_NAME, required = false) final String examName,
            @RequestParam(name = API.PARAM_GROUP_ID, required = false) final String groupUUID,
            @RequestParam(name = API.PARAM_GROUP_NAME, required = false) final String groupName,
            @RequestParam(name = API.PARAM_SESSION_ID, required = false) final String sessionUUID,
            @RequestParam(name = API.PARAM_FROM_TIME, required = false) final Long fromTime,
            @RequestParam(name = API.PARAM_TO_TIME, required = false) final Long toTime,
            @RequestParam(name = Page.ATTR_PAGE_NUMBER, required = false) final Integer pageNumber,
            @RequestParam(name = Page.ATTR_PAGE_SIZE, required = false) final Integer pageSize,
            @RequestParam(name = Page.ATTR_SORT, required = false) final String sortBy,
            final HttpServletRequest request) {

        final FilterMap filterMap = new FilterMap(request);

        if (Utils.hasMetaDataCriteria(filterMap)) {

            // paging must be applied programmatically after getting sorted big page form DB
            preProcessGroupCriteria(filterMap);
            this.paginationService.setUnlimitedPagination(
                    sortBy,
                    SessionRecordDynamicSqlSupport.sessionRecord.tableNameAtRuntime());

            final Collection<SessionSearchResult> result = querySessions(filterMap)
                    .getOrThrow();

            return this.paginationService.buildPageFromList(
                    pageNumber,
                    pageSize,
                    sortBy,
                    result,
                    all -> (List<SessionSearchResult>) all);
        }

        // paging can be applied on DB level (SQL)
        return this.paginationService.getPageOf(
                        pageNumber,
                        pageSize,
                        sortBy,
                        SessionRecordDynamicSqlSupport.sessionRecord.tableNameAtRuntime(),
                        () -> preProcessGroupCriteria(filterMap),
                        () -> querySessions(filterMap))
                .getOrThrow();
    }


    @Operation(
            summary = "Get grouped screenshot data list for specific session",
            description = "Groups all the screenshots for a given session. Currently the grouping is done via the Metadata 'WindowTitle'. "
                    +
                    "By providing additional metadata, only screenshot data with the given metadata will be returned",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = { @Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE) }),
            parameters = {
                    @Parameter(
                            name = API.PARAM_SESSION_ID,
                            description = "The UUID of the session to get the timeline group data for",
                            required = true),
                    @Parameter(
                            name = API.SCREENSHOT_META_DATA_APPLICATION,
                            description = "The search filter criteria for screenshot application metadata. This is used for full-text search in screenshot meta data",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.SCREENSHOT_META_DATA_BROWSER_TITLE,
                            description = "The search filter criteria for browser title metadata. This is used for full-text search in screenshot meta data",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.SCREENSHOT_META_DATA_BROWSER_URL,
                            description = "The search filter criteria for screenshot browser URL metadata. This is used for full-text search in screenshot meta data",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.SCREENSHOT_META_DATA_ACTIVE_WINDOW_TITLE,
                            description = "The search filter criteria for screenshot active window title metadata. This is used for full-text search in screenshot meta data",
                            in = ParameterIn.QUERY,
                            required = false),
                    @Parameter(
                            name = API.SCREENSHOT_META_DATA_USER_ACTION,
                            description = "The search filter criteria for screenshot user action metadata. This is used for full-text search in screenshot meta data",
                            in = ParameterIn.QUERY,
                            required = false)
            })
    @RequestMapping(
            path = API.TIMELINE_SEARCH_ENDPOINT + API.SESSION_ID_PATH_SEGMENT,
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TimelineViewData getTimelineViewData(
            @PathVariable(name = API.PARAM_SESSION_ID, required = true) final String sessionUUID,
            final HttpServletRequest request) {
        final FilterMap filterMap = new FilterMap(request);
        filterMap.putIfAbsent(API.PARAM_SESSION_ID, sessionUUID);

        return this.groupingService.groupDataForTimeline(filterMap).getOrThrow();
    }

    @Operation(
            summary = "Get a list of timestamps of a sessions",
            description = "Returns a list of session timestamps that were captured later or earlier than the specified timestamp.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = { @Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE) }),
            parameters = {
                    @Parameter(
                            name = API.PARAM_SESSION_ID,
                            description = "UUID of the session",
                            required = true),
                    @Parameter(
                            name = API.PARAM_TIMESTAMP,
                            description = "start timestamp",
                            required = true),
                    @Parameter(
                            name = API.PARAM_DIRECTION,
                            description = "Direction in which the list of timestamps is being returned",
                            required = true),
            })
    @RequestMapping(
            path = API.SCREENSHOT_TIMESTAMPS_ENDPOINT + API.SESSION_ID_TIMESTAMP_PATH_SEGMENT + API.DIRECTION_PATH_SEGMENT,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Long> getScreenshotTimestamps(
            @PathVariable(name = API.PARAM_SESSION_ID, required = true) final String sessionUUID,
            @PathVariable(name = API.PARAM_TIMESTAMP, required = true) final String timestamp,
            @PathVariable(name = API.PARAM_DIRECTION, required = true) final PageSortOrder sortOrder){
        return new ArrayList<>(this.screenshotDataDAO
                .getScreenshotTimestamps(
                        sessionUUID,
                        (StringUtils.isNotBlank(timestamp))
                                ? Long.parseLong(timestamp)
                                : null,
                        sortOrder)
                .getOrThrow());
    }

    private void preProcessGroupCriteria(final FilterMap filterMap) {
        final Collection<Long> readPrivilegedPredication = this.groupService.getReadPrivilegedPredication();
        final String ids = StringUtils.join(
                this.groupDAO
                        .getGroupIdsWithExamData(filterMap, readPrivilegedPredication)
                        .getOrThrow()
                        .stream()
                        .filter(Objects::nonNull)
                        .map(String::valueOf)
                        .collect(Collectors.toList()),
                Constants.LIST_SEPARATOR);

        filterMap.putIfAbsent(Domain.SESSION.ATTR_GROUP_ID, ids);
    }

    private Result<Collection<SessionSearchResult>> querySessions(final FilterMap filterMap) {
        final String groupIds = filterMap.getString(Domain.SESSION.ATTR_GROUP_ID);
        if (groupIds != null && StringUtils.isBlank(groupIds)) {
            return Result.of(Collections.emptyList());
        } else {
            return this.proctoringService.searchSessions(filterMap);
        }
    }

    private Result<Collection<ScreenshotSearchResult>> queryScreenshots(final FilterMap filterMap) {
        final String groupIds = filterMap.getString(Domain.SESSION.ATTR_GROUP_ID);
        if (groupIds != null && StringUtils.isBlank(groupIds)) {
            return Result.of(Collections.emptyList());
        } else {
            return this.proctoringService.searchScreenshots(filterMap);
        }
    }

    private Result<List<Date>> queryMatchingDaysForSessionSearch(final FilterMap filterMap) {
        final String groupIds = filterMap.getString(Domain.SESSION.ATTR_GROUP_ID);
        if (groupIds != null && StringUtils.isBlank(groupIds)) {
            return Result.of(Collections.emptyList());
        } else {
            return this.proctoringService.queryMatchingDaysForSessionSearch(filterMap);
        }
    }

}
