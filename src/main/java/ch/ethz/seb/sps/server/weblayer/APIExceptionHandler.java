/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import ch.ethz.seb.sps.domain.api.APIError;
import ch.ethz.seb.sps.domain.api.APIErrorException;
import ch.ethz.seb.sps.server.datalayer.dao.DuplicateEntityException;
import ch.ethz.seb.sps.server.datalayer.dao.NoResourceFoundException;
import ch.ethz.seb.sps.server.servicelayer.BeanValidationException;
import ch.ethz.seb.sps.utils.Utils;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class APIExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(APIExceptionHandler.class);

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

        log.error("Error intercepted at API response error handler: {}", apiError, ex);

        return new ResponseEntity<>(apiError, headers, status);
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

        log.error("Error intercepted at API response error handler: {}", apiError, ex);

        return new ResponseEntity<>(apiError, null, apiError.errorType.httpStatus);
    }

    @ExceptionHandler(APIErrorException.class)
    public ResponseEntity<Object> handleAPIErrorException(
            final APIErrorException ex,
            final WebRequest request) {

        log.warn("Error intercepted at API response error handler: {}", ex.error);

        return new ResponseEntity<>(ex.error, null, ex.error.errorType.httpStatus);
    }

    @ExceptionHandler(BeanValidationException.class)
    public ResponseEntity<Object> handleBeanValidationException(
            final BeanValidationException ex,
            final WebRequest request) {

        final Map<String, String> attributes = new HashMap<>();
        addRequestAttributes(request, attributes);
        ex.getBindingResult().getFieldErrors().stream().forEach(fieldError -> {
            final String field = fieldError.getField();
            // TODO more?
            attributes.put("field", field);
            attributes.put("validation-code", fieldError.getCode());
            attributes.put("validation-message", fieldError.getDefaultMessage());
        });
        ex.getBindingResult()
                .getModel()
                .entrySet()
                .forEach(entry -> attributes.put(entry.getKey(), String.valueOf(entry.getValue())));

        final APIError apiError = new APIError(
                APIError.APIErrorType.FIELD_VALIDATION,
                request.getDescription(false),
                ex.getMessage(),
                attributes,
                null);

        log.info("Error intercepted at API response error handler: {}", apiError);

        return new ResponseEntity<>(apiError, null, apiError.errorType.httpStatus);
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
                APIError.APIErrorType.BAD_REQUEST,
                request.getDescription(false),
                ex.getMessage(),
                attributes,
                null);

        log.warn("Error intercepted at API response error handler: {}", apiError);

        return new ResponseEntity<>(apiError, null, apiError.errorType.httpStatus);
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
                APIError.APIErrorType.NO_RESOURCE_FOUND,
                request.getDescription(false),
                ex.getMessage(),
                attributes,
                null);

        log.info("Resource not found for request: {}", apiError);

        return new ResponseEntity<>(apiError, null, apiError.errorType.httpStatus);
    }

    @ExceptionHandler(ActivationMismatchException.class)
    public ResponseEntity<Object> handleActivationMismatchException(
            final ActivationMismatchException ex,
            final WebRequest request) {

        if (log.isDebugEnabled()) {
            log.debug("Activation requested but was already active/inactive: {}", ex.getMessage());
        }
        return new ResponseEntity<>(null, null, HttpStatus.OK);
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

        log.warn("Error intercepted at API response error handler: {}", apiError);

        return new ResponseEntity<>(apiError, null, apiError.errorType.httpStatus);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<Object> handleUnsupportedOperationException(
            final UnsupportedOperationException ex,
            final WebRequest request) {

        final APIError apiError = new APIError(
                APIError.APIErrorType.METHOD_NOT_ALLOWED,
                request.getDescription(false),
                ex.getMessage(),
                null,
                null);

        log.warn("Error intercepted at API response error handler: {}", apiError);

        return new ResponseEntity<>(apiError, null, apiError.errorType.httpStatus);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(
            final BadCredentialsException ex,
            final WebRequest request) {

        final Map<String, String> attributes = new HashMap<>();
        addRequestAttributes(request, attributes);

        final APIError apiError = new APIError(
                APIError.APIErrorType.ACCESS_DENIED,
                request.getDescription(false),
                ex.getMessage(),
                attributes,
                null);

        log.error("Error intercepted at API response error handler: {}", apiError, ex);

        return new ResponseEntity<>(apiError, null, apiError.errorType.httpStatus);
    }

    private void addRequestAttributes(final WebRequest request, final Map<String, String> attributes) {
        final Principal userPrincipal = request.getUserPrincipal();

        Map<String, String[]> parameterMap = new HashMap<>(request.getParameterMap());
        parameterMap.remove("password");
        attributes.put("request-parameter", Utils.toString(parameterMap));
    
        if (userPrincipal != null) {
            attributes.put("request-user", userPrincipal.getName());
        }
    }

}
