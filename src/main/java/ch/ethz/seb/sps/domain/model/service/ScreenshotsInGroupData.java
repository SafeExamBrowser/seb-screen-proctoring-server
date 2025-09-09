/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model.service;

import ch.ethz.seb.sps.domain.Domain.SEB_GROUP;
import ch.ethz.seb.sps.domain.model.PageSortOrder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScreenshotsInGroupData {

    public static final String ATTR_EXAM = "exam";
    public static final String ATTR_NUM_OF_LIVE_SESSIONS = "numberOfLiveSessions";
    public static final String ATTR_NUM_OF_SESSIONS = "numberOfSessions";
    public static final String ATTR_PAGE_NUMBER = "pageNumber";
    public static final String ATTR_PAGE_SIZE = "pageSize";
    public static final String ATTR_SORT_BY = "sortBy";
    public static final String ATTR_SORT_ORDER = "sortOrder";
    public static final String ATTR_SCREENSHOTS = "screenshots";

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(SEB_GROUP.ATTR_UUID)
    public final String groupUUID;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(SEB_GROUP.ATTR_NAME)
    public final String groupName;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(SEB_GROUP.ATTR_DESCRIPTION)
    public final String groupDescription;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(ATTR_NUM_OF_LIVE_SESSIONS)
    public final int numberOfLiveSessions;

    @Schema(accessMode = AccessMode.READ_ONLY)
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

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(ATTR_SCREENSHOTS)
    public final List<ScreenshotViewData> screenshots;

    @JsonProperty(ATTR_EXAM)
    public final ExamViewData examViewData;

    public ScreenshotsInGroupData(
            @JsonProperty(SEB_GROUP.ATTR_UUID) final String groupUUID,
            @JsonProperty(SEB_GROUP.ATTR_NAME) final String groupName,
            @JsonProperty(SEB_GROUP.ATTR_DESCRIPTION) final String groupDescription,
            @JsonProperty(ATTR_NUM_OF_LIVE_SESSIONS) final int numberOfLiveSessions,
            @JsonProperty(ATTR_NUM_OF_SESSIONS) final int numberOfSessions,
            @JsonProperty(ATTR_PAGE_NUMBER) final int pageNumber,
            @JsonProperty(ATTR_PAGE_SIZE) final int pageSize,
            @JsonProperty(ATTR_SORT_BY) final String sortBy,
            @JsonProperty(ATTR_SORT_ORDER) final PageSortOrder sortOrder,
            @JsonProperty(ATTR_SCREENSHOTS) final List<ScreenshotViewData> sessionDataPage,
            @JsonProperty(ATTR_EXAM) final ExamViewData examViewData) {

        this.groupUUID = groupUUID;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.numberOfLiveSessions = numberOfLiveSessions;
        this.numberOfSessions = numberOfSessions;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
        this.screenshots = sessionDataPage;
        this.examViewData = examViewData;
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

    public int getNumberOfLiveSessions() {
        return this.numberOfLiveSessions;
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
        final ScreenshotsInGroupData other = (ScreenshotsInGroupData) obj;
        return Objects.equals(this.groupUUID, other.groupUUID);
    }

    @Override
    public String toString() {
        return "ScreenshotsInGroupData [groupUUID=" + this.groupUUID +
                ", groupName=" + this.groupName +
                ", groupDescription=" + this.groupDescription +
                ", numberOfLiveSessions=" + this.numberOfLiveSessions +
                ", numberOfSessions=" + this.numberOfSessions +
                ", pageNumber=" + this.pageNumber +
                ", pageSize=" + this.pageSize +
                ", sortBy=" + this.sortBy +
                ", sortOrder=" + this.sortOrder +
                ", screenshots=" + this.screenshots +
                "]";
    }

}
