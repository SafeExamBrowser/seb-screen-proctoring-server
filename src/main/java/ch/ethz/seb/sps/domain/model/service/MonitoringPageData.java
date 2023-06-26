/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model.service;

import java.util.Arrays;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.ethz.seb.sps.domain.Domain.SEB_GROUP;
import ch.ethz.seb.sps.domain.model.PageSortOrder;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MonitoringPageData {

    public static final String ATTR_ACTIVE_SESSIONS = "activeSessions";
    public static final String ATTR_PAGE_NUMBER = "pageNumber";
    public static final String ATTR_PAGE_SIZE = "pageSize";
    public static final String ATTR_SORT_BY = "sortBy";
    public static final String ATTR_SORT_ORDER = "sortOrder";
    public static final String ATTR_SESSION_DATA_PAGE = "sessionDataPage";

    @JsonProperty(SEB_GROUP.ATTR_UUID)
    public final String groupUUID;

    @JsonProperty(ATTR_ACTIVE_SESSIONS)
    public final int activeSessions;

    @JsonProperty(ATTR_PAGE_NUMBER)
    public final int pageNumber;

    @JsonProperty(ATTR_PAGE_SIZE)
    public final int pageSize;

    @JsonProperty(ATTR_SORT_BY)
    public final String sortBy;

    @JsonProperty(ATTR_SORT_ORDER)
    public final PageSortOrder sortOrder;

    @JsonProperty(ATTR_SESSION_DATA_PAGE)
    public final SessionData[] sessionDataPage;

    public MonitoringPageData(
            @JsonProperty(SEB_GROUP.ATTR_UUID) final String groupUUID,
            @JsonProperty(ATTR_ACTIVE_SESSIONS) final int activeSessions,
            @JsonProperty(ATTR_PAGE_NUMBER) final int pageNumber,
            @JsonProperty(ATTR_PAGE_SIZE) final int pageSize,
            @JsonProperty(ATTR_SORT_BY) final String sortBy,
            @JsonProperty(ATTR_SORT_ORDER) final PageSortOrder sortOrder,
            @JsonProperty(ATTR_SESSION_DATA_PAGE) final SessionData[] sessionDataPage) {

        this.groupUUID = groupUUID;
        this.activeSessions = activeSessions;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
        this.sessionDataPage = sessionDataPage;
    }

    public String getGroupUUID() {
        return this.groupUUID;
    }

    public int getActiveSessions() {
        return this.activeSessions;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public String getSortBy() {
        return this.sortBy;
    }

    public PageSortOrder getSortOrder() {
        return this.sortOrder;
    }

    public SessionData[] getSessionDataPage() {
        return this.sessionDataPage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.groupUUID);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final MonitoringPageData other = (MonitoringPageData) obj;
        return Objects.equals(this.groupUUID, other.groupUUID);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("MonitoringPageData [groupUUID=");
        builder.append(this.groupUUID);
        builder.append(", activeSessions=");
        builder.append(this.activeSessions);
        builder.append(", pageNumber=");
        builder.append(this.pageNumber);
        builder.append(", pageSize=");
        builder.append(this.pageSize);
        builder.append(", sortBy=");
        builder.append(this.sortBy);
        builder.append(", sortOrder=");
        builder.append(this.sortOrder);
        builder.append(", sessionDataPage=");
        builder.append(Arrays.toString(this.sessionDataPage));
        builder.append("]");
        return builder.toString();
    }

}
