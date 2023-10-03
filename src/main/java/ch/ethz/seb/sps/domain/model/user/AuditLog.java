/*
 * Copyright (c) 2018 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.ethz.seb.sps.domain.Domain.AUDIT_LOG;
import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.EntityType;

public class AuditLog implements Entity {

    public enum AuditLogType {
        REGISTER,
        CREATE,
        IMPORT,
        EXPORT,
        MODIFY,
        PASSWORD_CHANGE,
        DEACTIVATE,
        ACTIVATE,
        FINISHED,
        DELETE,
        LOGIN,
        LOGOUT,
        ARCHIVE
    }

    public static final String ATTR_USER_NAME = "username";
    public static final String FILTER_ATTR_USER_NAME = ATTR_USER_NAME;
    public static final String FILTER_ATTR_FROM = "from";
    public static final String FILTER_ATTR_TO = "to";
    public static final String FILTER_ATTR_FROM_TO = "from_to";

    public static final String FILTER_ATTR_ACTIVITY_TYPES = "activity_types";
    public static final String FILTER_ATTR_ENTITY_TYPES = "entity_types";

    @JsonProperty(AUDIT_LOG.ATTR_ID)
    public final Long id;
    @JsonProperty(AUDIT_LOG.ATTR_USER_UUID)
    public final String userUUID;
    @JsonProperty(ATTR_USER_NAME)
    public final String username;
    @JsonProperty(AUDIT_LOG.ATTR_TIMESTAMP)
    public final Long timestamp;
    @JsonProperty(AUDIT_LOG.ATTR_ACTIVITY_TYPE)
    public final AuditLogType activityType;
    @JsonProperty(AUDIT_LOG.ATTR_ENTITY_TYPE)
    public final EntityType entityType;
    @JsonProperty(AUDIT_LOG.ATTR_ENTITY_ID)
    public final String entityId;
    @JsonProperty(AUDIT_LOG.ATTR_MESSAGE)
    public final String message;

    @JsonCreator
    public AuditLog(
            @JsonProperty(AUDIT_LOG.ATTR_ID) final Long id,
            @JsonProperty(AUDIT_LOG.ATTR_USER_UUID) final String userUUID,
            @JsonProperty(ATTR_USER_NAME) final String username,
            @JsonProperty(AUDIT_LOG.ATTR_TIMESTAMP) final Long timestamp,
            @JsonProperty(AUDIT_LOG.ATTR_ACTIVITY_TYPE) final AuditLogType activityType,
            @JsonProperty(AUDIT_LOG.ATTR_ENTITY_TYPE) final EntityType entityType,
            @JsonProperty(AUDIT_LOG.ATTR_ENTITY_ID) final String entityId,
            @JsonProperty(AUDIT_LOG.ATTR_MESSAGE) final String message) {

        this.id = id;
        this.userUUID = userUUID;
        this.username = username;
        this.timestamp = timestamp;
        this.activityType = activityType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.message = message;
    }

    @Override
    public EntityType entityType() {
        return EntityType.AUDIT_LOG;
    }

    @Override
    public String getModelId() {
        return (this.id != null)
                ? String.valueOf(this.id)
                : null;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.username;
    }

    public String getUserUuid() {
        return this.userUUID;
    }

    public String getUsername() {
        return this.username;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public AuditLogType getActivityType() {
        return this.activityType;
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    public String getEntityId() {
        return this.entityId;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("UserActivityLog [id=");
        builder.append(this.id);
        builder.append(", userUUID=");
        builder.append(this.userUUID);
        builder.append(", username=");
        builder.append(this.username);
        builder.append(", timestamp=");
        builder.append(this.timestamp);
        builder.append(", activityType=");
        builder.append(this.activityType);
        builder.append(", entityType=");
        builder.append(this.entityType);
        builder.append(", entityId=");
        builder.append(this.entityId);
        builder.append(", message=");
        builder.append(this.message);
        builder.append("]");
        return builder.toString();
    }

}
