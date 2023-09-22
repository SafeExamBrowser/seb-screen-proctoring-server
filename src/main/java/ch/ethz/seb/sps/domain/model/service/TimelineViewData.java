package ch.ethz.seb.sps.domain.model.service;

import java.util.List;

public class TimelineViewData {

    public final String sessionUUID;
    public List<TimelineGroupData> timelineGroupDataList;

    public TimelineViewData(String sessionUUID, List<TimelineGroupData> timelineGroupDataList) {
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
