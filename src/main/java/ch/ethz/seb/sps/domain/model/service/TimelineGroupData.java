package ch.ethz.seb.sps.domain.model.service;

import java.util.List;

public class TimelineGroupData {

    public final int groupOrder;
    public final String groupName;
    public final List<TimelineScreenshotData> timelineScreenshotDataList;

    public TimelineGroupData(int groupOrder, String groupName, List<TimelineScreenshotData> timelineScreenshotDataList) {
        this.groupOrder = groupOrder;
        this.groupName = groupName;
        this.timelineScreenshotDataList = timelineScreenshotDataList;
    }

    public int getGroupOrder() {
        return groupOrder;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<TimelineScreenshotData> getTimelineScreenshotDataList() {
        return timelineScreenshotDataList;
    }

    public void addItemToScreenshotData(TimelineScreenshotData timelineScreenshotData){
        this.timelineScreenshotDataList.add(timelineScreenshotData);
    }

}
