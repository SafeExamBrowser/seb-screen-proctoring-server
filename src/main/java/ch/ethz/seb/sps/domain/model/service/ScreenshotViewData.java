/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model.service;

import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.ethz.seb.sps.domain.Domain.SESSION;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class ScreenshotViewData {

    public static final String ATTR_TIMESTAMP = "timestamp";
    public static final String ATTR_SESSION = "session";
    public static final String ATTR_LATEST_IMAGE_LINK = "latestImageLink";
    public static final String ATTR_IMAGE_LINK = "imageLink";
    public static final String ATTR_META_DATA = "metaData";

    @JsonProperty(ATTR_TIMESTAMP)
    public final Long timestamp;

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

    @JsonProperty(ATTR_LATEST_IMAGE_LINK)
    public final String latestImageLink;

    @JsonProperty(ATTR_IMAGE_LINK)
    public final String imageLink;

    @JsonProperty(ATTR_META_DATA)
    public final Map<String, String> metaData;

    public ScreenshotViewData(
            @JsonProperty(ATTR_TIMESTAMP) final Long timestamp,
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

        this.timestamp = timestamp;
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
        final StringBuilder builder = new StringBuilder();
        builder.append("ScreenshotViewData [timestamp=");
        builder.append(this.timestamp);
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
        builder.append(", latestImageLink=");
        builder.append(this.latestImageLink);
        builder.append(", imageLink=");
        builder.append(this.imageLink);
        builder.append(", metaData=");
        builder.append(this.metaData);
        builder.append("]");
        return builder.toString();
    }

}