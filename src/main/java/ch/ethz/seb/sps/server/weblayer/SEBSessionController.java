/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.service.Session;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import ch.ethz.seb.sps.server.ServiceConfig;
import ch.ethz.seb.sps.server.servicelayer.ScreenshotStoreService;
import ch.ethz.seb.sps.server.servicelayer.SessionService;
import ch.ethz.seb.sps.server.servicelayer.SessionServiceHealthControl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("${sps.api.session.endpoint.v1}" + API.SESSION_ENDPOINT)
@SecurityRequirement(name = WebServiceConfig.SWAGGER_AUTH_SEB_CLIENT)
public class SEBSessionController {

    private static final Logger log = LoggerFactory.getLogger(SEBSessionController.class);

    private final SessionService sessionService;
    private final ScreenshotStoreService screenshotStoreService;
    private final SessionServiceHealthControl sessionServiceHealthControl;
    private final Executor uploadExecutor;

    public SEBSessionController(
            final SessionService sessionService,
            final ScreenshotStoreService screenshotStoreService,
            final SessionServiceHealthControl sessionServiceHealthControl,
            @Qualifier(value = ServiceConfig.SCREENSHOT_UPLOAD_API_EXECUTOR) final Executor uploadExecutor) {

        this.sessionService = sessionService;
        this.screenshotStoreService = screenshotStoreService;
        this.sessionServiceHealthControl = sessionServiceHealthControl;
        this.uploadExecutor = uploadExecutor;
    }

    @Operation(
            summary = "Create a new SEB session for a specific SEB client that is valid during proctoring",
            description = "",
            parameters = {
                    @Parameter(
                            name = API.GROUP_HEADER_UUID,
                            description = "The SEB group identifier where the session shall belong to. Usually SEB gets this by configuration.",
                            in = ParameterIn.HEADER,
                            required = true,
                            allowEmptyValue = false),
                    @Parameter(
                            name = API.SESSION_HEADER_SEB_USER_NAME,
                            description = "The user name or identifier. This might be the students LMS account name or account identifier",
                            in = ParameterIn.HEADER,
                            required = false,
                            allowEmptyValue = false),
                    @Parameter(
                            name = API.SESSION_HEADER_SEB_IP,
                            description = "The SEB device Internet protocol (IP) address if available",
                            in = ParameterIn.HEADER,
                            required = false,
                            allowEmptyValue = false),
                    @Parameter(
                            name = API.SESSION_HEADER_SEB_MACHINE_NAME,
                            description = "The SEB device name if available",
                            in = ParameterIn.HEADER,
                            required = false,
                            allowEmptyValue = false),
                    @Parameter(
                            name = API.SESSION_HEADER_SEB_OS,
                            description = "The SEB device operating system name if available",
                            in = ParameterIn.HEADER,
                            required = false,
                            allowEmptyValue = false),
                    @Parameter(
                            name = API.SESSION_HEADER_SEB_VERSION,
                            description = "The SEB application version",
                            in = ParameterIn.HEADER,
                            required = false,
                            allowEmptyValue = false),
                    @Parameter(
                            name = Domain.SCREENSHOT_DATA.ATTR_IMAGE_FORMAT,
                            description = "The image format of the session. This is used as default image format for the while session.",
                            in = ParameterIn.HEADER,
                            required = false,
                            allowEmptyValue = true,
                            schema = @Schema(
                                    allowableValues = { "png", "webp" },
                                    defaultValue = "png")

                    )
            })
    @RequestMapping(method = RequestMethod.POST)
    public void createNewSession(
            @RequestHeader(name = API.GROUP_HEADER_UUID, required = true) final String groupUUID,
            @RequestHeader(name = API.SESSION_HEADER_SEB_USER_NAME, required = false) final String userSessionName,
            @RequestHeader(name = API.SESSION_HEADER_SEB_IP, required = false) final String clientIP,
            @RequestHeader(name = API.SESSION_HEADER_SEB_MACHINE_NAME, required = false) final String clientMachineName,
            @RequestHeader(name = API.SESSION_HEADER_SEB_OS, required = false) final String clientOSName,
            @RequestHeader(name = API.SESSION_HEADER_SEB_VERSION, required = false) final String clientVersion,
            @RequestHeader(name = Domain.SCREENSHOT_DATA.ATTR_IMAGE_FORMAT, required = false) final String format,
            final HttpServletResponse response) {

        final ImageFormat imageFormat = (StringUtils.isNotEmpty(format))
                ? ImageFormat.byName(format)
                : ImageFormat.PNG;

        final Session session = this.sessionService
                .createNewSession(
                        groupUUID,
                        userSessionName,
                        clientIP,
                        clientMachineName,
                        clientOSName,
                        clientVersion,
                        imageFormat)
                .getOrThrow();

        response.setHeader(API.SESSION_HEADER_UUID, session.uuid);
        response.setStatus(HttpStatus.OK.value());
    }

    @Operation(
            summary = "Update a particular SEB session with new information",
            description = "",
            parameters = {
                    @Parameter(
                            name = API.PARAM_MODEL_ID,
                            description = "The SEB session id (UUID)",
                            in = ParameterIn.PATH,
                            required = true,
                            allowEmptyValue = false),
                    @Parameter(
                            name = API.SESSION_HEADER_SEB_USER_NAME,
                            description = "The user name or identifier. This might be the students LMS account name or account identifier",
                            in = ParameterIn.HEADER,
                            required = false,
                            allowEmptyValue = false),
                    @Parameter(
                            name = API.SESSION_HEADER_SEB_IP,
                            description = "The SEB device Internet protocol (IP) address if available",
                            in = ParameterIn.HEADER,
                            required = false,
                            allowEmptyValue = false),
                    @Parameter(
                            name = API.SESSION_HEADER_SEB_MACHINE_NAME,
                            description = "The SEB device name if available",
                            in = ParameterIn.HEADER,
                            required = false,
                            allowEmptyValue = false),
                    @Parameter(
                            name = API.SESSION_HEADER_SEB_OS,
                            description = "The SEB device operating system name if available",
                            in = ParameterIn.HEADER,
                            required = false,
                            allowEmptyValue = false),
                    @Parameter(
                            name = API.SESSION_HEADER_SEB_VERSION,
                            description = "The SEB application version",
                            in = ParameterIn.HEADER,
                            required = false,
                            allowEmptyValue = false)
            })
    @RequestMapping(
            path = API.PARAM_MODEL_PATH_SEGMENT,
            method = RequestMethod.PUT)
    public void updateSessionData(
            @PathVariable(name = API.PARAM_MODEL_ID, required = true) final String sessionUUID,
            @RequestHeader(name = API.SESSION_HEADER_SEB_USER_NAME, required = false) final String userSessionName,
            @RequestHeader(name = API.SESSION_HEADER_SEB_IP, required = false) final String clientIP,
            @RequestHeader(name = API.SESSION_HEADER_SEB_MACHINE_NAME, required = false) final String clientMachineName,
            @RequestHeader(name = API.SESSION_HEADER_SEB_OS, required = false) final String clientOSName,
            @RequestHeader(name = API.SESSION_HEADER_SEB_VERSION, required = false) final String clientVersion,
            final HttpServletResponse response) {

        final Session session = this.sessionService
                .updateSessionData(
                        sessionUUID,
                        userSessionName,
                        clientIP,
                        clientMachineName,
                        clientOSName,
                        clientVersion)
                .getOrThrow();

        response.setHeader(API.SESSION_HEADER_UUID, session.uuid);
        response.setStatus(HttpStatus.OK.value());
    }

    @Operation(
            summary = "Close a particular SEB session. Closed SEB sessions cannot receive screenshots anymore.",
            description = "",
            parameters = {
                    @Parameter(
                            name = API.PARAM_MODEL_ID,
                            description = "The SEB session id (UUID)",
                            in = ParameterIn.PATH,
                            required = true,
                            allowEmptyValue = false)
            })
    @RequestMapping(
            path = API.PARAM_MODEL_PATH_SEGMENT,
            method = RequestMethod.DELETE,
            consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void closeSession(@PathVariable(name = API.PARAM_MODEL_ID, required = true) final String sessionUUID) {
        this.sessionService.closeSession(sessionUUID);
    }

    @Operation(
            summary = "Upload of a SEB screenshot",
            description = "",
            parameters = {
                    @Parameter(
                            name = API.PARAM_MODEL_ID,
                            description = "The SEB session id (UUID)",
                            in = ParameterIn.PATH,
                            required = true,
                            allowEmptyValue = false),
                    @Parameter(
                            name = Domain.SCREENSHOT_DATA.ATTR_TIMESTAMP,
                            description = "The Unix time-stamp of the screenshot in milliseconds since 1.1.1970 (UTC)",
                            in = ParameterIn.HEADER,
                            required = true,
                            allowEmptyValue = false),
                    @Parameter(
                            name = Domain.SCREENSHOT_DATA.ATTR_IMAGE_FORMAT,
                            description = "The image format of the screenshot. This is only needed if the image format differs from the default format defined in the SEB session.",
                            in = ParameterIn.HEADER,
                            required = false),
                    @Parameter(
                            name = Domain.SCREENSHOT_DATA.ATTR_META_DATA,
                            description = "The meta data of the screenshot in JSON format (Name-Value-Pair/Map/Dictionary)",
                            in = ParameterIn.HEADER,
                            required = false)
            },
            requestBody = @RequestBody(
                    required = true,
                    description = "The image binary data",
                    content = { @Content(
                            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(format = "binary")) }))
    @RequestMapping(
            path = API.PARAM_MODEL_PATH_SEGMENT + API.SCREENSHOT_ENDPOINT,
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public CompletableFuture<Void> postScreenshot(
            @PathVariable(name = API.PARAM_MODEL_ID) final String sessionUUID,
            @RequestHeader(name = Domain.SCREENSHOT_DATA.ATTR_TIMESTAMP) final Long timestamp,
            @RequestHeader(name = Domain.SCREENSHOT_DATA.ATTR_IMAGE_FORMAT, required = false) final String format,
            @RequestHeader(name = Domain.SCREENSHOT_DATA.ATTR_META_DATA, required = false) final String metadata,
            final HttpServletRequest request,
            final HttpServletResponse response) {

        // TODO remove this after testing
        if (log.isDebugEnabled()) {
            log.debug("Meta Data received: {}", metadata);
        }

        return CompletableFuture.runAsync(
                () -> {
                    try {

                        // TODO inject session cache and get session by sessionUUID and check if it is still active (not terminated)
                        //      if inactive throw error for SEB client to notify session closed

                        final ImageFormat imageFormat = (StringUtils.isNotEmpty(format))
                                ? ImageFormat.byName(format)
                                : null;

                        this.screenshotStoreService.storeScreenshot(
                                sessionUUID,
                                timestamp,
                                imageFormat,
                                metadata,
                                request.getInputStream());

                        response.setStatus(HttpStatus.OK.value());

                        final int overallLoadIndicator = this.sessionServiceHealthControl.getOverallLoadIndicator();
                        response.setHeader(API.SPS_SERVER_HEALTH, String.valueOf(overallLoadIndicator));

                    } catch (final Exception e) {
                        log.error("Failed to store screenshot: ", e);
                        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    }
                },
                this.uploadExecutor);
    }

}
