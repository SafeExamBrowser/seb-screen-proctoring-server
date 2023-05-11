/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.server.servicelayer.ScreenshotService;
import ch.ethz.seb.sps.server.servicelayer.SessionService;

@RestController
@RequestMapping("${sps.api.session.endpoint.v1}" + API.SESSION_ENDPOINT)
public class SessionController {

    private static final Logger log = LoggerFactory.getLogger(SessionController.class);

    private final SessionService sessionService;
    private final ScreenshotService screenshotService;

    public SessionController(
            final SessionService sessionService,
            final ScreenshotService screenshotService) {

        this.sessionService = sessionService;
        this.screenshotService = screenshotService;
    }

    @RequestMapping(
            path = API.PARAM_MODEL_PATH_SEGMENT + API.SESSION_SCREENSHOT_LATEST_ENDPOINT,
            method = RequestMethod.GET,
            produces = {
                    MediaType.IMAGE_PNG_VALUE,
                    MediaType.IMAGE_JPEG_VALUE,
                    MediaType.IMAGE_GIF_VALUE,
                    MediaType.APPLICATION_OCTET_STREAM_VALUE })
    public CompletableFuture<Void> getLatestScreenshot(
            @PathVariable(name = API.PARAM_MODEL_ID) final String sessionId,
            final HttpServletResponse response) {

        final CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        try {

            this.screenshotService.streamLatestScreenshot(
                    sessionId,
                    response.getOutputStream(),
                    completableFuture);

            response.setStatus(HttpStatus.OK.value());

        } catch (final Exception e) {
            log.error("Failed to stream image file: ", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return completableFuture;
    }

    @RequestMapping(
            method = RequestMethod.POST)
    public void createNewSession(
            @RequestHeader(name = API.GROUP_HEADER_UUID, required = true) final String groupUUID,
            final HttpServletResponse response) {

        final String sessionUUID = this.sessionService
                .createNewSession(groupUUID)
                .getOrThrow();

        response.setHeader(API.SESSION_HEADER_UUID, sessionUUID);
        response.setStatus(HttpStatus.OK.value());
    }

    @RequestMapping(
            path = API.PARAM_MODEL_PATH_SEGMENT + API.SESSION_SCREENSHOT_ENDPOINT,
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public CompletableFuture<Void> postScreenshot(
            @PathVariable(name = API.PARAM_MODEL_ID, required = true) final String sessionUUID,
            @RequestHeader(name = Domain.SCREENSHOT_DATA.ATTR_TIMESTAMP) final Long timestamp,
            @RequestHeader(name = Domain.SCREENSHOT_DATA.ATTR_IMAGE_FORMAT, required = false) final String format,
            @RequestHeader(name = Domain.SCREENSHOT_DATA.ATTR_META_DATA, required = false) final String metadata,
            final HttpServletRequest request,
            final HttpServletResponse response) {

        final CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        try {

            this.screenshotService.storeScreenshot(
                    sessionUUID,
                    timestamp,
                    format,
                    metadata,
                    request.getInputStream(),
                    completableFuture);

            response.setStatus(HttpStatus.OK.value());
        } catch (final Exception e) {
            log.error("Failed to store screenshot: ", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return completableFuture;
    }

    @RequestMapping(
            path = API.PARAM_MODEL_PATH_SEGMENT,
            method = RequestMethod.DELETE,
            consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void closeSession(@PathVariable(name = API.PARAM_MODEL_ID, required = true) final String sessionUUID) {
        this.sessionService.closeSession(sessionUUID);
    }

}
