/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.api.API.PrivilegeType;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.server.ServiceConfig;
import ch.ethz.seb.sps.server.servicelayer.ScreenshotFetchService;
import ch.ethz.seb.sps.server.servicelayer.UserService;
import ch.ethz.seb.sps.utils.Constants;

@RestController
@RequestMapping("${sps.api.admin.endpoint.v1}" + API.SESSION_ENDPOINT)
public class AdminProctorController {

    private static final Logger log = LoggerFactory.getLogger(AdminProctorController.class);

    private final ScreenshotFetchService screenshotFetchService;
    private final Executor downloadExecutor;
    private final UserService userService;

    public AdminProctorController(
            final ScreenshotFetchService screenshotFetchService,
            final UserService userService,
            @Qualifier(value = ServiceConfig.SCREENSHOT_DOWNLOAD_API_EXECUTOR) final Executor downloadExecutor) {

        this.screenshotFetchService = screenshotFetchService;
        this.downloadExecutor = downloadExecutor;
        this.userService = userService;
    }

    @RequestMapping(
            path = API.PARAM_MODEL_PATH_SEGMENT + API.SESSION_SCREENSHOT_LATEST_ENDPOINT,
            method = RequestMethod.GET,
            produces = {
                    MediaType.IMAGE_PNG_VALUE,
                    Constants.MIME_TYPE_IMAGE_WEBP,
                    MediaType.APPLICATION_OCTET_STREAM_VALUE })
    public CompletableFuture<Void> getLatestScreenshot(
            @PathVariable(name = API.PARAM_MODEL_ID) final String sessionId,
            final HttpServletResponse response) {

        return CompletableFuture.runAsync(
                () -> {

                    this.userService.check(PrivilegeType.READ, EntityType.SESSION);

                    try {

                        this.screenshotFetchService.streamLatestScreenshot(
                                sessionId,
                                response.getOutputStream());

                        response.setStatus(HttpStatus.OK.value());

                    } catch (final Exception e) {
                        log.error("Failed to stream image file: ", e);
                        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    }
                },
                this.downloadExecutor);
    }

}
