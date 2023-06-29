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

import ch.ethz.seb.sps.domain.api.API.PrivilegeType;
import ch.ethz.seb.sps.domain.api.APIError.APIErrorType;
import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.user.UserInfo;

public class APIErrorException extends RuntimeException {

    private static final long serialVersionUID = -5404410347204934738L;
    public final APIError error;

    public APIErrorException(final APIError error) {
        super(error.message);
        this.error = error;
    }

    public APIErrorException(final APIError.APIErrorType type) {
        super(type.name());
        this.error = new APIError(type, null, null, null, null);
    }

    public static APIErrorException ofMissingAttribute(final String attrName, final String request) {

        return new APIErrorException(new APIError(
                APIErrorType.BAD_REQUEST,
                request,
                "missing attribute: " + attrName,
                null,
                null));
    }

    public static APIErrorException ofPermissionDenied(
            final EntityType entityType,
            final PrivilegeType privilegeType) {

        return ofPermissionDenied(entityType, privilegeType, null);
    }

    public static APIErrorException ofPermissionDenied(
            final EntityType entityType,
            final PrivilegeType privilegeType,
            final UserInfo userInfo) {

        final Map<String, String> attributes = new HashMap<>();
        attributes.put("resource-type", entityType.name());
        attributes.put("access-type", privilegeType.name());
        if (userInfo != null) {
            attributes.put("user-uuid", userInfo.uuid);
            attributes.put("user-name", userInfo.name);
        }
        return new APIErrorException(new APIError(
                APIErrorType.FORBIDDEN_ACCESS,
                "checkPermission",
                "User doesn't have the right to access the resource",
                attributes,
                null));
    }

    public static APIErrorException entityValidationError(
            final String message,
            final Entity entity,
            final String request) {

        final Map<String, String> attributes = new HashMap<>();
        attributes.put("resource-type", entity.entityType().name());
        attributes.put("resource-id", entity.getModelId());
        attributes.put("resource-name", entity.getName());

        return new APIErrorException(new APIError(
                APIErrorType.FIELD_VALIDATION,
                request,
                message,
                attributes,
                null));
    }

    public static APIErrorException ofIllegalState(
            final String request,
            final String message,
            final Entity entity) {

        final Map<String, String> attributes = new HashMap<>();
        attributes.put("resource-type", entity.entityType().name());
        attributes.put("resource-id", entity.getModelId());
        attributes.put("resource-name", entity.getName());

        return new APIErrorException(new APIError(
                APIErrorType.INTEGRITY_VIOLATION,
                request,
                message,
                attributes,
                null));
    }

    public static APIErrorException ofFieldValidation(
            final String request,
            final String fieldName,
            final String fieldId) {

        final Map<String, String> attributes = new HashMap<>();
        attributes.put("field-id", fieldId);
        attributes.put("field-name", fieldName);

        return new APIErrorException(new APIError(
                APIErrorType.INTEGRITY_VIOLATION,
                request,
                fieldId,
                attributes,
                null));
    }

    public static APIErrorException ofFieldValidation(
            final String request,
            final Collection<APIError> filedErrors) {

        return new APIErrorException(new APIError(
                APIErrorType.INTEGRITY_VIOLATION,
                request,
                null,
                null,
                filedErrors));
    }

    public static APIErrorException notFound(
            final EntityType entityType,
            final String groupUUID,
            final String message) {

        return new APIErrorException(new APIError(
                APIErrorType.BAD_REQUEST,
                "get entity: " + entityType,
                message,
                null,
                null));
    }

}
