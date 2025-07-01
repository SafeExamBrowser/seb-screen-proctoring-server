package ch.ethz.seb.sps.domain.model.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TimelineViewData {

    public static final String ATTR_TIMELINE_SESSION_UUID = "sessionUUID";
    public static final String ATTR_TIMELINE_GROUP_DATA_LIST = "timelineGroupDataList";

    @JsonProperty(ATTR_TIMELINE_SESSION_UUID)
    public final String sessionUUID;

    @JsonProperty(ATTR_TIMELINE_GROUP_DATA_LIST)
    public List<TimelineGroupData> timelineGroupDataList;

    public TimelineViewData(
            @JsonProperty(ATTR_TIMELINE_SESSION_UUID) String sessionUUID,
            @JsonProperty(ATTR_TIMELINE_GROUP_DATA_LIST) List<TimelineGroupData> timelineGroupDataList) {

        this.sessionUUID = sessionUUID;
        this.timelineGroupDataList = timelineGroupDataList;
    }

    public String getSessionUUID() {
        return sessionUUID;
    }

    public List<TimelineGroupData> getTimelineGroupDataList() {
        return timelineGroupDataList;
    }

    public void setTimelineGroupDataList(List<TimelineGroupData> timelineGroupDataList) {
        this.timelineGroupDataList = timelineGroupDataList;
    }
}
