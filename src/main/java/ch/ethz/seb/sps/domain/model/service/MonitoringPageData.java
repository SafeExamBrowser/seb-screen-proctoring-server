/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model.service;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.ethz.seb.sps.domain.Domain.SEB_GROUP;
import ch.ethz.seb.sps.domain.model.PageSortOrder;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MonitoringPageData {

    public static final String ATTR_NUM_OF_SESSIONS = "numberOfSessions";
    public static final String ATTR_PAGE_NUMBER = "pageNumber";
    public static final String ATTR_PAGE_SIZE = "pageSize";
    public static final String ATTR_SORT_BY = "sortBy";
    public static final String ATTR_SORT_ORDER = "sortOrder";
    public static final String ATTR_SCREENSHOTS = "screenshots";

    @JsonProperty(SEB_GROUP.ATTR_UUID)
    public final String groupUUID;

    @JsonProperty(SEB_GROUP.ATTR_NAME)
    public final String groupName;

    @JsonProperty(SEB_GROUP.ATTR_DESCRIPTION)
    public final String groupDescription;

    @JsonProperty(ATTR_NUM_OF_SESSIONS)
    public final int numberOfSessions;

    @JsonProperty(ATTR_PAGE_NUMBER)
    public final int pageNumber;

    @JsonProperty(ATTR_PAGE_SIZE)
    public final int pageSize;

    @JsonProperty(ATTR_SORT_BY)
    public final String sortBy;

    @JsonProperty(ATTR_SORT_ORDER)
    public final PageSortOrder sortOrder;

    @JsonProperty(ATTR_SCREENSHOTS)
    public final List<ScreenshotViewData> screenshots;

    public MonitoringPageData(
            @JsonProperty(SEB_GROUP.ATTR_UUID) final String groupUUID,
            @JsonProperty(SEB_GROUP.ATTR_NAME) final String groupName,
            @JsonProperty(SEB_GROUP.ATTR_DESCRIPTION) final String groupDescription,
            @JsonProperty(ATTR_NUM_OF_SESSIONS) final int numberOfSessions,
            @JsonProperty(ATTR_PAGE_NUMBER) final int pageNumber,
            @JsonProperty(ATTR_PAGE_SIZE) final int pageSize,
            @JsonProperty(ATTR_SORT_BY) final String sortBy,
            @JsonProperty(ATTR_SORT_ORDER) final PageSortOrder sortOrder,
            @JsonProperty(ATTR_SCREENSHOTS) final List<ScreenshotViewData> sessionDataPage) {

        this.groupUUID = groupUUID;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.numberOfSessions = numberOfSessions;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
        this.screenshots = sessionDataPage;
    }

    public String getGroupUUID() {
        return this.groupUUID;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public String getGroupDescription() {
        return this.groupDescription;
    }

    public int getNumberOfSessions() {
        return this.numberOfSessions;
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

    public List<ScreenshotViewData> getScreenshots() {
        return this.screenshots;
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
        builder.append(", groupName=");
        builder.append(this.groupName);
        builder.append(", groupDescription=");
        builder.append(this.groupDescription);
        builder.append(", numberOfSessions=");
        builder.append(this.numberOfSessions);
        builder.append(", pageNumber=");
        builder.append(this.pageNumber);
        builder.append(", pageSize=");
        builder.append(this.pageSize);
        builder.append(", sortBy=");
        builder.append(this.sortBy);
        builder.append(", sortOrder=");
        builder.append(this.sortOrder);
        builder.append(", screenshots=");
        builder.append(this.screenshots);
        builder.append("]");
        return builder.toString();
    }

}
