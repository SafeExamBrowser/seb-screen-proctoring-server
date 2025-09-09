/*
 * Copyright (c) 2024 ETH Zürich, IT Services
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

import ch.ethz.seb.sps.domain.Domain.SCREENSHOT_DATA;
import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScreenshotData implements Entity {

    public static final String SCREEN_PREFIX = "screen_";

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(SCREENSHOT_DATA.ATTR_ID)
    public final Long id;

    @JsonProperty(SCREENSHOT_DATA.ATTR_SESSION_UUID)
    public final String sessionUUID;

    @JsonProperty(SCREENSHOT_DATA.ATTR_TIMESTAMP)
    public final Long timestamp;

    @JsonProperty(SCREENSHOT_DATA.ATTR_IMAGE_FORMAT)
    public final ImageFormat imageFormat;

    @JsonProperty(SCREENSHOT_DATA.ATTR_META_DATA)
    public final String metaData;

    @JsonCreator
    public ScreenshotData(
            @JsonProperty(SCREENSHOT_DATA.ATTR_ID) final Long id,
            @JsonProperty(SCREENSHOT_DATA.ATTR_SESSION_UUID) final String sessionUUID,
            @JsonProperty(SCREENSHOT_DATA.ATTR_TIMESTAMP) final Long timestamp,
            @JsonProperty(SCREENSHOT_DATA.ATTR_IMAGE_FORMAT) final ImageFormat imageFormat,
            @JsonProperty(SCREENSHOT_DATA.ATTR_META_DATA) final String metaData) {

        this.id = id;
        this.sessionUUID = sessionUUID;
        this.timestamp = timestamp;
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
        return EntityType.SCREENSHOT_DATA;
    }

    @Override
    public String getName() {
        return SCREEN_PREFIX + this.id;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    public String getSessionUUID() {
        return this.sessionUUID;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public ImageFormat getImageFormat() {
        return this.imageFormat;
    }

    public String getMetaData() {
        return this.metaData;
    }

    @Override
    public int hashCode() {
        return Objects.hash(EntityType.SCREENSHOT_DATA, this.id);
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
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "ScreenshotData [id=" + this.id +
                ", sessionUUID=" + this.sessionUUID +
                ", timestamp=" + this.timestamp +
                ", imageFormat=" + this.imageFormat +
                ", metaData=" + this.metaData +
                "]";
    }

}
