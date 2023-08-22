/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model.service;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.ethz.seb.sps.domain.Domain.SESSION;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionSearchResult implements Entity {

    public static final String ATTR_GROUP_CREATION_TIME = "groupCreationTime";

    public static final String ATTR_START_TIME = "startTime";
    public static final String ATTR_END_TIME = "endTime";

    @JsonProperty(API.PARAM_GROUP_ID)
    public final String groupUUID;

    @JsonProperty(API.PARAM_GROUP_NAME)
    public final String groupName;

    @JsonProperty(ATTR_GROUP_CREATION_TIME)
    public final Long groupCreationTime;

    @JsonProperty(API.PARAM_SESSION_ID)
    public final String sessionUUID;

    @JsonProperty(ATTR_START_TIME)
    public final Long startTime;

    @JsonProperty(ATTR_END_TIME)
    public final Long endTime;

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

    @JsonProperty("nrOfScreenshots")
    public final Integer nrOfScreenshots;

    @JsonCreator
    public SessionSearchResult(
            @JsonProperty(API.PARAM_GROUP_ID) final String groupUUID,
            @JsonProperty(API.PARAM_GROUP_NAME) final String groupName,
            @JsonProperty(ATTR_GROUP_CREATION_TIME) final Long groupCreationTime,
            @JsonProperty(API.PARAM_SESSION_ID) final String sessionUUID,
            @JsonProperty(ATTR_START_TIME) final Long startTime,
            @JsonProperty(ATTR_END_TIME) final Long endTime,
            @JsonProperty(SESSION.ATTR_CLIENT_NAME) final String clientName,
            @JsonProperty(SESSION.ATTR_CLIENT_IP) final String clientIP,
            @JsonProperty(SESSION.ATTR_CLIENT_MACHINE_NAME) final String clientMachineName,
            @JsonProperty(SESSION.ATTR_CLIENT_OS_NAME) final String clientOSName,
            @JsonProperty(SESSION.ATTR_CLIENT_VERSION) final String clientVersion,
            @JsonProperty(SESSION.ATTR_IMAGE_FORMAT) final ImageFormat imageFormat,
            @JsonProperty("nrOfScreenshots") final Integer nrOfScreenshots) {

        this.groupUUID = groupUUID;
        this.groupName = groupName;
        this.groupCreationTime = groupCreationTime;
        this.sessionUUID = sessionUUID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.clientName = clientName;
        this.clientIP = clientIP;
        this.clientMachineName = clientMachineName;
        this.clientOSName = clientOSName;
        this.clientVersion = clientVersion;
        this.imageFormat = imageFormat;
        this.nrOfScreenshots = nrOfScreenshots;
    }

    public SessionSearchResult(
            final Session session,
            final Group group,
            final int nrOfScreenshots) {

        this.groupUUID = group.uuid;
        this.groupName = group.name;
        this.groupCreationTime = group.creationTime;
        this.sessionUUID = session.uuid;
        this.startTime = session.creationTime;
        this.endTime = session.terminationTime;
        this.clientName = session.clientName;
        this.clientIP = session.clientIP;
        this.clientMachineName = session.clientMachineName;
        this.clientOSName = session.clientOSName;
        this.clientVersion = session.clientVersion;
        this.imageFormat = session.imageFormat;
        this.nrOfScreenshots = nrOfScreenshots;
    }

    @Override
    public String getModelId() {
        return this.sessionUUID;
    }

    @Override
    public EntityType entityType() {
        return EntityType.SESSION;
    }

    @Override
    public String getName() {
        return this.sessionUUID;
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

    public Integer getNrOfScreenshots() {
        return this.nrOfScreenshots;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.sessionUUID);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SessionSearchResult other = (SessionSearchResult) obj;
        return Objects.equals(this.sessionUUID, other.sessionUUID);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("SessionSearchResult [groupUUID=");
        builder.append(this.groupUUID);
        builder.append(", groupName=");
        builder.append(this.groupName);
        builder.append(", groupCreationTime=");
        builder.append(this.groupCreationTime);
        builder.append(", sessionUUID=");
        builder.append(this.sessionUUID);
        builder.append(", startTime=");
        builder.append(this.startTime);
        builder.append(", endTime=");
        builder.append(this.endTime);
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
        builder.append(", nrOfScreenshots=");
        builder.append(this.nrOfScreenshots);
        builder.append("]");
        return builder.toString();
    }

}
