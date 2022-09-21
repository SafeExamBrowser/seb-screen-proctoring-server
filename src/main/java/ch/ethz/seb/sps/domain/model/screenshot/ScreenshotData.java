/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model.screenshot;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.ethz.seb.sps.domain.Domain.SCREENSHOT_DATA;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.EntityType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScreenshotData implements Entity {

    @JsonProperty(API.PARAM_ENTITY_TYPE)
    public final EntityType entityType = EntityType.SCREENSHOT_DATA;

    @JsonProperty(SCREENSHOT_DATA.ATTR_ID)
    public final Long id;

    @JsonProperty(SCREENSHOT_DATA.ATTR_SCREENSHOT_ID)
    public final Long screenshotId;

    @JsonProperty(SCREENSHOT_DATA.ATTR_SESSION_ID)
    public final String sessionId;

    @JsonProperty(SCREENSHOT_DATA.ATTR_GROUP_ID)
    public final Long groupId;

    @JsonProperty(SCREENSHOT_DATA.ATTR_TIMESTAMP)
    public final Long timestamp;

    @JsonProperty(SCREENSHOT_DATA.ATTR_IMAGE_URL)
    public final String imageURL;

    @JsonProperty(SCREENSHOT_DATA.ATTR_IMAGE_FORMAT)
    public final String imageFormat;

    @JsonProperty(SCREENSHOT_DATA.ATTR_META_DATA)
    public final String metaData;

    @JsonCreator
    public ScreenshotData(
            @JsonProperty(SCREENSHOT_DATA.ATTR_ID) final Long id,
            @JsonProperty(SCREENSHOT_DATA.ATTR_SCREENSHOT_ID) final Long screenshotId,
            @JsonProperty(SCREENSHOT_DATA.ATTR_SESSION_ID) final String sessionId,
            @JsonProperty(SCREENSHOT_DATA.ATTR_GROUP_ID) final Long groupId,
            @JsonProperty(SCREENSHOT_DATA.ATTR_TIMESTAMP) final Long timestamp,
            @JsonProperty(SCREENSHOT_DATA.ATTR_IMAGE_URL) final String imageURL,
            @JsonProperty(SCREENSHOT_DATA.ATTR_IMAGE_FORMAT) final String imageFormat,
            @JsonProperty(SCREENSHOT_DATA.ATTR_META_DATA) final String metaData) {

        this.id = id;
        this.screenshotId = screenshotId;
        this.sessionId = sessionId;
        this.groupId = groupId;
        this.timestamp = timestamp;
        this.imageURL = imageURL;
        this.imageFormat = imageFormat;
        this.metaData = metaData;
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
        return this.imageURL;
    }

    public Long getId() {
        return this.id;
    }

    public Long getScreenshotId() {
        return this.screenshotId;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public Long getGroupId() {
        return this.groupId;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public String getImageURL() {
        return this.imageURL;
    }

    public String getImageFormat() {
        return this.imageFormat;
    }

    public String getMetaData() {
        return this.metaData;
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
        final ScreenshotData other = (ScreenshotData) obj;
        return this.entityType == other.entityType && Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("ScreenshotData [entityType=");
        builder.append(this.entityType);
        builder.append(", id=");
        builder.append(this.id);
        builder.append(", screenshotId=");
        builder.append(this.screenshotId);
        builder.append(", sessionId=");
        builder.append(this.sessionId);
        builder.append(", groupId=");
        builder.append(this.groupId);
        builder.append(", timestamp=");
        builder.append(this.timestamp);
        builder.append(", imageURL=");
        builder.append(this.imageURL);
        builder.append(", imageFormat=");
        builder.append(this.imageFormat);
        builder.append(", metaData=");
        builder.append(this.metaData);
        builder.append("]");
        return builder.toString();
    }

}
