/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.ethz.seb.sps.utils.Utils;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class APIError {

    public enum APIErrorType {
        UNEXPECTED(HttpStatus.INTERNAL_SERVER_ERROR),
        ACCESS_DENIED(HttpStatus.UNAUTHORIZED),
        FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN),
        NO_RESOURCE_FOUND(HttpStatus.NOT_FOUND),
        BAD_REQUEST(HttpStatus.BAD_REQUEST),
        INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST),
        FIELD_VALIDATION(HttpStatus.BAD_REQUEST),
        PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST),
        METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED);

        public final HttpStatus httpStatus;

        private APIErrorType(final HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
        }

    }

    @JsonProperty("errorType")
    public final APIError.APIErrorType errorType;
    @JsonProperty("request")
    public final String request;
    @JsonProperty("message")
    public final String message;
    @JsonProperty("attributes")
    public final Map<String, String> attributes;
    @JsonProperty("otherErrors")
    public final Collection<APIError> otherErrors;

    public APIError(
            final APIErrorType errorType,
            final String request,
            final String message) {

        this.errorType = errorType;
        this.request = request;
        this.message = message;
        this.attributes = null;
        this.otherErrors = null;
    }

    @JsonCreator
    public APIError(
            @JsonProperty("errorType") final APIErrorType errorType,
            @JsonProperty("request") final String request,
            @JsonProperty("message") final String message,
            @JsonProperty("attributes") final Map<String, String> attributes,
            @JsonProperty("otherErrors") final Collection<APIError> otherErrors) {

        this.errorType = errorType;
        this.request = request;
        this.message = message;
        this.attributes = Utils.immutableMapOf(attributes);
        this.otherErrors = Utils.immutableCollectionOf(otherErrors);
    }

    public APIError.APIErrorType getErrorType() {
        return this.errorType;
    }

    public String getMessage() {
        return this.message;
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("APIError [errorType=");
        builder.append(this.errorType);
        builder.append(", message=");
        builder.append(this.message);
        builder.append(", attributes=");
        builder.append(this.attributes);
        builder.append("]");
        return builder.toString();
    }

    public static APIError fieldValidationError(final FieldError fieldError) {
        final String fieldName = fieldError.getField();
        final String fieldId = fieldError.getDefaultMessage();
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("field-id", fieldId);
        attributes.put("field-name", fieldName);

        return new APIError(
                APIErrorType.INTEGRITY_VIOLATION,
                fieldError.getObjectName(),
                fieldId,
                attributes,
                null);
    }

}