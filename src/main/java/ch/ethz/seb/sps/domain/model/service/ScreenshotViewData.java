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

import ch.ethz.seb.sps.domain.Domain.SESSION;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class ScreenshotViewData {

    public static final String ATTR_TIMESTAMP = "timestamp";
    public static final String ATTR_START_TIME = "startTime";
    public static final String ATTR_END_TIME = "endTime";
    public static final String ATTR_ACTIVE = "active";
    public static final String ATTR_LATEST_IMAGE_LINK = "latestImageLink";
    public static final String ATTR_IMAGE_LINK = "imageLink";
    public static final String ATTR_META_DATA = "metaData";

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(ATTR_START_TIME)
    public final Long startTime;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(ATTR_TIMESTAMP)
    public final Long timestamp;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(ATTR_END_TIME)
    public final Long endTime;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(ATTR_ACTIVE)
    public final boolean active;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(SESSION.ATTR_UUID)
    public final String uuid;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(SESSION.ATTR_CLIENT_NAME)
    public final String clientName;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(SESSION.ATTR_CLIENT_IP)
    public final String clientIP;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(SESSION.ATTR_CLIENT_MACHINE_NAME)
    public final String clientMachineName;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(SESSION.ATTR_CLIENT_OS_NAME)
    public final String clientOSName;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(SESSION.ATTR_CLIENT_VERSION)
    public final String clientVersion;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(SESSION.ATTR_IMAGE_FORMAT)
    public final ImageFormat imageFormat;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(ATTR_LATEST_IMAGE_LINK)
    public final String latestImageLink;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(ATTR_IMAGE_LINK)
    public final String imageLink;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(ATTR_META_DATA)
    public final Map<String, String> metaData;

    @JsonCreator
    public ScreenshotViewData(
            @JsonProperty(ATTR_START_TIME) final Long startTime,
            @JsonProperty(ATTR_TIMESTAMP) final Long timestamp,
            @JsonProperty(ATTR_END_TIME) final Long endTime,
            @JsonProperty(ATTR_ACTIVE) final boolean active,
            @JsonProperty(SESSION.ATTR_UUID) final String uuid,
            @JsonProperty(SESSION.ATTR_CLIENT_NAME) final String clientName,
            @JsonProperty(SESSION.ATTR_CLIENT_IP) final String clientIP,
            @JsonProperty(SESSION.ATTR_CLIENT_MACHINE_NAME) final String clientMachineName,
            @JsonProperty(SESSION.ATTR_CLIENT_OS_NAME) final String clientOSName,
            @JsonProperty(SESSION.ATTR_CLIENT_VERSION) final String clientVersion,
            @JsonProperty(SESSION.ATTR_IMAGE_FORMAT) final ImageFormat imageFormat,
            @JsonProperty(ATTR_LATEST_IMAGE_LINK) final String latestImageLink,
            @JsonProperty(ATTR_IMAGE_LINK) final String imageLink,
            @JsonProperty(ATTR_META_DATA) final Map<String, String> metaData) {

        this.startTime = startTime;
        this.timestamp = timestamp;
        this.endTime = endTime;
        this.active = active;
        this.uuid = uuid;
        this.clientName = clientName;
        this.clientIP = clientIP;
        this.clientMachineName = clientMachineName;
        this.clientOSName = clientOSName;
        this.clientVersion = clientVersion;
        this.imageFormat = imageFormat;
        this.latestImageLink = latestImageLink;
        this.imageLink = imageLink;
        this.metaData = metaData;
    }

    public ScreenshotViewData(
            final Long startTime,
            final Long timestamp,
            final Long endTime,
            final String uuid,
            final String clientName,
            final String clientIP,
            final String clientMachineName,
            final String clientOSName,
            final String clientVersion,
            final ImageFormat imageFormat,
            final String latestImageLink,
            final String imageLink,
            final Map<String, String> metaData) {

        this.startTime = startTime;
        this.timestamp = timestamp;
        this.endTime = endTime;
        this.active = this.timestamp >= this.endTime;
        this.uuid = uuid;
        this.clientName = clientName;
        this.clientIP = clientIP;
        this.clientMachineName = clientMachineName;
        this.clientOSName = clientOSName;
        this.clientVersion = clientVersion;
        this.imageFormat = imageFormat;
        this.latestImageLink = latestImageLink;
        this.imageLink = imageLink;
        this.metaData = metaData;
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

    public String getLatestImageLink() {
        return this.latestImageLink;
    }

    public String getImageLink() {
        return this.imageLink;
    }

    public Map<String, String> getMetaData() {
        return this.metaData;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.timestamp, this.uuid);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ScreenshotViewData other = (ScreenshotViewData) obj;
        return Objects.equals(this.timestamp, other.timestamp) && Objects.equals(this.uuid, other.uuid);
    }

    @Override
    public String toString() {
        return "ScreenshotViewData [startTime=" + this.startTime +
                ", timestamp=" + this.timestamp +
                ", endTime=" + this.endTime +
                ", uuid=" + this.uuid +
                ", clientName=" + this.clientName +
                ", clientIP=" + this.clientIP +
                ", clientMachineName=" + this.clientMachineName +
                ", clientOSName=" + this.clientOSName +
                ", clientVersion=" + this.clientVersion +
                ", imageFormat=" + this.imageFormat +
                ", latestImageLink=" + this.latestImageLink +
                ", imageLink=" + this.imageLink +
                ", metaData=" + this.metaData +
                "]";
    }

}