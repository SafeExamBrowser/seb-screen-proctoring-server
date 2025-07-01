/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model.service;

import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.ethz.seb.sps.domain.Domain.SCREENSHOT_DATA;
import ch.ethz.seb.sps.domain.Domain.SESSION;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScreenshotSearchResult {

    public static final String ATTR_GROUP_CREATION_TIME = "groupCreationTime";

    public static final String ATTR_SESSION_START_TIME = "sessionStartTime";
    public static final String ATTR_SESSION_END_TIME = "sessionEndTime";
    public static final String ATTR_SESSION_CLIENT_NAME = "sessionClientName";
    public static final String ATTR_SESSION_CLIENT_IP = "sessionClientIP";
    public static final String ATTR_SESSION_CLIENT_MACHINE_NAME = "sessionClientMachineName";
    public static final String ATTR_SESSION_CLIENT_OS_NAME = "sessionClientOSName";
    public static final String ATTR_SESSION_CLIENT_VERSION = "sessionClientVersion";

    public static final String ATTR_IMAGE_ID = "imageId";
    public static final String ATTR_META_DATA = "metaData";
    public static final String ATTR_TIMESTAMP = "imageTimestamp";

    @JsonProperty(API.PARAM_GROUP_UUID)
    public final String groupUUID;

    @JsonProperty(API.PARAM_GROUP_NAME)
    public final String groupName;

    @JsonProperty(ATTR_GROUP_CREATION_TIME)
    public final Long groupCreationTime;

    @JsonProperty(API.PARAM_SESSION_ID)
    public final String sessionUUID;

    @JsonProperty(ATTR_SESSION_START_TIME)
    public final Long startTime;

    @JsonProperty(ATTR_SESSION_END_TIME)
    public final Long endTime;

    @JsonProperty(ATTR_SESSION_CLIENT_NAME)
    public final String clientName;

    @JsonProperty(ATTR_SESSION_CLIENT_IP)
    public final String clientIP;

    @JsonProperty(ATTR_SESSION_CLIENT_MACHINE_NAME)
    public final String clientMachineName;

    @JsonProperty(ATTR_SESSION_CLIENT_OS_NAME)
    public final String clientOSName;

    @JsonProperty(ATTR_SESSION_CLIENT_VERSION)
    public final String clientVersion;

    @JsonProperty(ATTR_IMAGE_ID)
    public final Long imageId;

    @JsonProperty(ATTR_TIMESTAMP)
    public final Long timestamp;

    @JsonProperty(SESSION.ATTR_IMAGE_FORMAT)
    public final ImageFormat imageFormat;

    @JsonProperty(ATTR_META_DATA)
    public final Map<String, String> metaData;

    @JsonCreator
    public ScreenshotSearchResult(
            @JsonProperty(API.PARAM_GROUP_UUID) final String groupUUID,
            @JsonProperty(API.PARAM_GROUP_NAME) final String groupName,
            @JsonProperty(ATTR_GROUP_CREATION_TIME) final Long groupCreationTime,
            @JsonProperty(API.PARAM_SESSION_ID) final String sessionUUID,
            @JsonProperty(ATTR_SESSION_START_TIME) final Long startTime,
            @JsonProperty(ATTR_SESSION_END_TIME) final Long endTime,
            @JsonProperty(ATTR_SESSION_CLIENT_NAME) final String clientName,
            @JsonProperty(ATTR_SESSION_CLIENT_IP) final String clientIP,
            @JsonProperty(ATTR_SESSION_CLIENT_MACHINE_NAME) final String clientMachineName,
            @JsonProperty(ATTR_SESSION_CLIENT_OS_NAME) final String clientOSName,
            @JsonProperty(ATTR_SESSION_CLIENT_VERSION) final String clientVersion,
            @JsonProperty(ATTR_IMAGE_ID) final Long imageId,
            @JsonProperty(ATTR_TIMESTAMP) final Long timestamp,
            @JsonProperty(SCREENSHOT_DATA.ATTR_IMAGE_FORMAT) final ImageFormat imageFormat,
            @JsonProperty(ATTR_META_DATA) final Map<String, String> metaData) {

        this.groupUUID = groupUUID;
        this.groupName = groupName;
        this.groupCreationTime = groupCreationTime;
        this.sessionUUID = sessionUUID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timestamp = timestamp;
        this.clientName = clientName;
        this.clientIP = clientIP;
        this.clientMachineName = clientMachineName;
        this.clientOSName = clientOSName;
        this.clientVersion = clientVersion;
        this.imageId = imageId;
        this.imageFormat = imageFormat;
        this.metaData = metaData;
    }

    public ScreenshotSearchResult(
            final Long imageId,
            final Group group,
            final Session session,
            final Long timestamp,
            final ImageFormat imageFormat,
            final Map<String, String> metaData) {

        this.groupUUID = group.uuid;
        this.groupName = group.name;
        this.groupCreationTime = group.creationTime;
        this.sessionUUID = session.uuid;
        this.startTime = session.creationTime;
        this.endTime = session.terminationTime;
        this.timestamp = timestamp;
        this.clientName = session.clientName;
        this.clientIP = session.clientIP;
        this.clientMachineName = session.clientMachineName;
        this.clientOSName = session.clientOSName;
        this.clientVersion = session.clientVersion;
        this.imageId = imageId;
        this.imageFormat = imageFormat;
        this.metaData = metaData;
    }

    public String getGroupUUID() {
        return this.groupUUID;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public Long getGroupCreationTime() {
        return this.groupCreationTime;
    }

    public String getSessionUUID() {
        return this.sessionUUID;
    }

    public Long getStartTime() {
        return this.startTime;
    }

    public Long getEndTime() {
        return this.endTime;
    }

    public Long getTimestamp() {
        return this.timestamp;
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

    public Long getImageId() {
        return this.imageId;
    }

    public ImageFormat getImageFormat() {
        return this.imageFormat;
    }

    public Map<String, String> getMetaData() {
        return this.metaData;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.imageId);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ScreenshotSearchResult other = (ScreenshotSearchResult) obj;
        return Objects.equals(this.imageId, other.imageId);
    }

    @Override
    public String toString() {
        return "ScreenshotSearchResult [groupUUID=" + this.groupUUID +
                ", groupName=" + this.groupName +
                ", groupCreationTime=" + this.groupCreationTime +
                ", sessionUUID=" + this.sessionUUID +
                ", startTime=" + this.startTime +
                ", endTime=" + this.endTime +
                ", timestamp=" + this.timestamp +
                ", clientName=" + this.clientName +
                ", clientIP=" + this.clientIP +
                ", clientMachineName=" + this.clientMachineName +
                ", clientOSName=" + this.clientOSName +
                ", clientVersion=" + this.clientVersion +
                ", imageId=" + this.imageId +
                ", imageFormat=" + this.imageFormat +
                ", metaData=" + this.metaData +
                "]";
    }

}
