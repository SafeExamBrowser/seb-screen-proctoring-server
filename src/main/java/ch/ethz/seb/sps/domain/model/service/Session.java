/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model.service;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.ethz.seb.sps.domain.Domain.SESSION;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.WithLifeCycle;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Session implements Entity, WithLifeCycle {

    public enum ImageFormat {
        PNG(0, "png"),
        WEBP(1, "webp");

        public final int code;
        public final String formatName;

        private ImageFormat(final int formatCode, final String formatName) {
            this.code = formatCode;
            this.formatName = formatName;
        }

        public static ImageFormat valueOf(final int code) {
            final ImageFormat[] values = ImageFormat.values();
            for (int i = 0; i < values.length; i++) {
                if (code == values[i].code) {
                    return values[i];
                }
            }
            return ImageFormat.PNG;
        }

        public static ImageFormat byName(final String format) {
            final ImageFormat[] values = ImageFormat.values();
            for (int i = 0; i < values.length; i++) {
                if (Objects.equals(format, values[i].formatName)) {
                    return values[i];
                }
            }
            return ImageFormat.PNG;
        }

        static String[] getAvailableFormats() {
            // TODO Auto-generated method stub
            return null;
        }

    }

    public static final String[] IMAGE_FORMATS = {};

    @JsonProperty(API.PARAM_ENTITY_TYPE)
    public final EntityType entityType = EntityType.SESSION;

    @JsonProperty(SESSION.ATTR_ID)
    public final Long id;

    @JsonProperty(SESSION.ATTR_GROUP_ID)
    @NotNull
    public final Long groupId;

    @JsonProperty(SESSION.ATTR_UUID)
    public final String uuid;

    @JsonProperty(SESSION.ATTR_CLIENT_NAME)
    public final String clientName;

    @JsonProperty(SESSION.ATTR_CLIENT_IP)
    public final String clientIP;

    @JsonProperty(SESSION.ATTR_CLIENT_MACHINE_NAME)
    public final String clientMachineName;

    @JsonProperty(SESSION.ATTR_CLIENT_OS_NAME)
    public final String clientOSName;

    @JsonProperty(SESSION.ATTR_CLIENT_VERSION)
    public final String clientVersion;

    @JsonProperty(SESSION.ATTR_IMAGE_FORMAT)
    public final ImageFormat imageFormat;

    @JsonProperty(SESSION.ATTR_CREATION_TIME)
    public final Long creationTime;

    @JsonProperty(SESSION.ATTR_LAST_UPDATE_TIME)
    public final Long lastUpdateTime;

    @JsonProperty(SESSION.ATTR_TERMINATION_TIME)
    public final Long terminationTime;

    @JsonCreator
    public Session(
            @JsonProperty(SESSION.ATTR_ID) final Long id,
            @JsonProperty(SESSION.ATTR_GROUP_ID) final Long groupId,
            @JsonProperty(SESSION.ATTR_UUID) final String uuid,
            @JsonProperty(SESSION.ATTR_CLIENT_NAME) final String clientName,
            @JsonProperty(SESSION.ATTR_CLIENT_IP) final String clientIP,
            @JsonProperty(SESSION.ATTR_CLIENT_MACHINE_NAME) final String clientMachineName,
            @JsonProperty(SESSION.ATTR_CLIENT_OS_NAME) final String clientOSName,
            @JsonProperty(SESSION.ATTR_CLIENT_VERSION) final String clientVersion,
            @JsonProperty(SESSION.ATTR_IMAGE_FORMAT) final ImageFormat imageFormat,
            @JsonProperty(SESSION.ATTR_CREATION_TIME) final Long creationTime,
            @JsonProperty(SESSION.ATTR_LAST_UPDATE_TIME) final Long lastUpdateTime,
            @JsonProperty(SESSION.ATTR_TERMINATION_TIME) final Long terminationTime) {

        this.id = id;
        this.groupId = groupId;
        this.uuid = uuid;
        this.clientName = clientName;
        this.clientIP = clientIP;
        this.clientMachineName = clientMachineName;
        this.clientOSName = clientOSName;
        this.clientVersion = clientVersion;
        this.imageFormat = (imageFormat != null) ? imageFormat : ImageFormat.PNG;
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.terminationTime = terminationTime;
    }

    @Override
    public String getModelId() {
        return (this.id != null)
                ? String.valueOf(this.id)
                : null;
    }

    @Override
    public EntityType entityType() {
        return this.entityType;
    }

    @Override
    public String getName() {
        return this.uuid;
    }

    public Long getId() {
        return this.id;
    }

    public Long getGroupId() {
        return this.groupId;
    }

    public String getUuid() {
        return this.uuid;
    }

    public String getClientName() {
        return this.clientName;
    }

    public String getClientIP() {
        return this.clientIP;
    }

    public String getClientMachineName() {
        return this.clientMachineName;
    }

    public String getClientOSName() {
        return this.clientOSName;
    }

    public String getClientVersion() {
        return this.clientVersion;
    }

    public ImageFormat getImageFormat() {
        return this.imageFormat;
    }

    @Override
    public Long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public Long getTerminationTime() {
        return this.terminationTime;
    }

    @Override
    public Long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.entityType, this.id);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Session other = (Session) obj;
        return this.entityType == other.entityType && Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Session [entityType=");
        builder.append(this.entityType);
        builder.append(", id=");
        builder.append(this.id);
        builder.append(", groupId=");
        builder.append(this.groupId);
        builder.append(", uuid=");
        builder.append(this.uuid);
        builder.append(", clientName=");
        builder.append(this.clientName);
        builder.append(", clientIP=");
        builder.append(this.clientIP);
        builder.append(", clientMachineName=");
        builder.append(this.clientMachineName);
        builder.append(", clientOSName=");
        builder.append(this.clientOSName);
        builder.append(", clientVersion=");
        builder.append(this.clientVersion);
        builder.append(", imageFormat=");
        builder.append(this.imageFormat);
        builder.append(", creationTime=");
        builder.append(this.creationTime);
        builder.append(", terminationTime=");
        builder.append(this.terminationTime);
        builder.append("]");
        return builder.toString();
    }

}
