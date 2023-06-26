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

@JsonIgnoreProperties(ignoreUnknown = true)
public final class SessionData {

    public static final String ATTR_TIMESTAMP = "timestamp";
    public static final String ATTR_SESSION = "session";
    public static final String ATTR_IMAGE_LINK = "imageLink";
    public static final String ATTR_META_DATA = "metaData";

    @JsonProperty(ATTR_TIMESTAMP)
    public final Long timestamp;

    @JsonProperty(ATTR_SESSION)
    public final Session session;

    @JsonProperty(ATTR_IMAGE_LINK)
    public final String imageLink;

    @JsonProperty(ATTR_META_DATA)
    public final Map<String, String> metaData;

    public SessionData(
            @JsonProperty(ATTR_TIMESTAMP) final Long timestamp,
            @JsonProperty(ATTR_SESSION) final Session session,
            @JsonProperty(ATTR_IMAGE_LINK) final String imageLink,
            @JsonProperty(ATTR_META_DATA) final Map<String, String> metaData) {

        this.timestamp = timestamp;
        this.session = session;
        this.imageLink = imageLink;
        this.metaData = metaData;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public Session getSession() {
        return this.session;
    }

    public String getImageLink() {
        return this.imageLink;
    }

    public Map<String, String> getMetaData() {
        return this.metaData;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.session, this.timestamp);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SessionData other = (SessionData) obj;
        return Objects.equals(this.session, other.session) && Objects.equals(this.timestamp, other.timestamp);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("SessionData [timestamp=");
        builder.append(this.timestamp);
        builder.append(", session=");
        builder.append(this.session);
        builder.append(", imageLink=");
        builder.append(this.imageLink);
        builder.append(", metaData=");
        builder.append(this.metaData);
        builder.append("]");
        return builder.toString();
    }

}