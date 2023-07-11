/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import ch.ethz.seb.sps.domain.api.APIError;
import ch.ethz.seb.sps.domain.api.APIErrorException;
import ch.ethz.seb.sps.domain.api.JSONMapper;
import ch.ethz.seb.sps.server.datalayer.dao.DuplicateEntityException;
import ch.ethz.seb.sps.server.datalayer.dao.NoResourceFoundException;
import ch.ethz.seb.sps.utils.Utils;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class APIExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(APIExceptionHandler.class);

    private final JSONMapper jsonMapper;

    public APIExceptionHandler(final JSONMapper jsonMapper) {
        super();
        this.jsonMapper = jsonMapper;
    }

    public void handleGenericExcpetionResponse(
            final Exception ex,
            final String request,
            final HttpServletResponse response) {

        final Map<String, String> attributes = new HashMap<>();
        // TODO more infos?

        final APIError apiError = new APIError(
                APIError.APIErrorType.UNEXPECTED,
                request,
                ex.getMessage(),
                attributes,
                null);

        String errorJSON = apiError.toString();
        try {
            errorJSON = this.jsonMapper.writeValueAsString(apiError);
        } catch (final Exception e) {
            log.error("Failed to parse APIError to String: ", e);
        }

        log.error("API error: {}", apiError, ex);

        try {
            final ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(Utils.toByteArray(errorJSON));
        } catch (final Exception e) {
            log.error("Failed to send error message to client: ", e);
        }
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            final Exception ex,
            final Object body,
            final HttpHeaders headers,
            final HttpStatus status,
            final WebRequest request) {

        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }

        final Map<String, String> attributes = new HashMap<>();
        attributes.put("http-status", (status != null) ? status.name() : "--");
        addRequestAttributes(request, attributes);
        attributes.put("body", String.valueOf(body));

        final APIError apiError = new APIError(
                APIError.APIErrorType.UNEXPECTED,
                request.getDescription(false),
                ex.getMessage(),
                attributes,
                null);

        String errorJSON = apiError.toString();
        try {
            errorJSON = this.jsonMapper.writeValueAsString(apiError);
        } catch (final Exception e) {
            log.error("Failed to parse APIError to String: ", e);
        }

        log.error("API error: {}", apiError, ex);

        return new ResponseEntity<>(errorJSON, headers, status);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(
            final RuntimeException ex,
            final WebRequest request) {

        final Map<String, String> attributes = new HashMap<>();
        addRequestAttributes(request, attributes);

        final APIError apiError = new APIError(
                APIError.APIErrorType.UNEXPECTED,
                request.getDescription(false),
                ex.getMessage(),
                attributes,
                null);

        String errorJSON = apiError.toString();
        try {
            errorJSON = this.jsonMapper.writeValueAsString(apiError);
        } catch (final Exception e) {
            log.error("Failed to parse APIError to String: ", e);
        }

        log.error("API error: {} cause: ", apiError, ex);

        return new ResponseEntity<>(errorJSON, null, apiError.errorType.httpStatus);
    }

    @ExceptionHandler(APIErrorException.class)
    public ResponseEntity<Object> handleAPIErrorException(
            final APIErrorException ex,
            final WebRequest request) {

        String errorJSON = ex.error.toString();
        try {
            errorJSON = this.jsonMapper.writeValueAsString(ex.error);
        } catch (final Exception e) {
            log.error("Failed to parse APIError to String: ", e);
        }

        log.error("API error: {}", ex.error);

        return new ResponseEntity<>(errorJSON, null, ex.error.errorType.httpStatus);
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<Object> handleDuplicateEntityException(
            final DuplicateEntityException ex,
            final WebRequest request) {

        final Map<String, String> attributes = new HashMap<>();
        addRequestAttributes(request, attributes);
        attributes.put("filedName", ex.fieldName);
        attributes.put("message", ex.message);
        attributes.put("entityType", ex.entityType.name());

        final APIError apiError = new APIError(
                APIError.APIErrorType.NO_RESOURCE_FOUND,
                request.getDescription(false),
                ex.getMessage(),
                attributes,
                null);

        String errorJSON = apiError.toString();
        try {
            errorJSON = this.jsonMapper.writeValueAsString(apiError);
        } catch (final Exception e) {
            log.error("Failed to parse APIError to String: ", e);
        }

        log.error("API error: {}", apiError);

        return new ResponseEntity<>(errorJSON, null, apiError.errorType.httpStatus);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> handleNoResourceFoundException(
            final NoResourceFoundException ex,
            final WebRequest request) {

        final Map<String, String> attributes = new HashMap<>();
        addRequestAttributes(request, attributes);
        attributes.put("message", ex.getMessage());
        attributes.put("entityType", ex.entityType.name());

        final APIError apiError = new APIError(
                APIError.APIErrorType.BAD_REQUEST,
                request.getDescription(false),
                ex.getMessage(),
                attributes,
                null);

        String errorJSON = apiError.toString();
        try {
            errorJSON = this.jsonMapper.writeValueAsString(apiError);
        } catch (final Exception e) {
            log.error("Failed to parse APIError to String: ", e);
        }

        log.error("API error: {}", apiError);

        return new ResponseEntity<>(errorJSON, null, apiError.errorType.httpStatus);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(
            final BadRequestException ex,
            final WebRequest request) {

        final Map<String, String> attributes = new HashMap<>();
        attributes.put("request", ex.request);
        addRequestAttributes(request, attributes);
        attributes.put("message", ex.getMessage());

        final APIError apiError = new APIError(
                APIError.APIErrorType.BAD_REQUEST,
                request.getDescription(false),
                ex.getMessage(),
                attributes,
                null);

        String errorJSON = apiError.toString();
        try {
            errorJSON = this.jsonMapper.writeValueAsString(apiError);
        } catch (final Exception e) {
            log.error("Failed to parse APIError to String: ", e);
        }

        log.error("API error: {}", apiError);

        return new ResponseEntity<>(errorJSON, null, apiError.errorType.httpStatus);
    }

    private void addRequestAttributes(final WebRequest request, final Map<String, String> attributes) {
        final Principal userPrincipal = request.getUserPrincipal();
        attributes.put("request-parameter", Utils.toString(request.getParameterMap()));
        //attributes.put("request-headers", Utils.immutableListOf(request.getHeaderNames()).toString());
        if (userPrincipal != null) {
            attributes.put("request-user", userPrincipal.getName());
        }
    }

}
