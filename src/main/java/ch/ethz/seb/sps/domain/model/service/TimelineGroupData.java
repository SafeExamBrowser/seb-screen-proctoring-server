package ch.ethz.seb.sps.domain.model.service;

import ch.ethz.seb.sps.domain.api.API;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TimelineGroupData {

    public static final String ATTR_GROUP_ORDER = "groupOrder";
    public static final String ATTR_TIMELINE_SCREENSHOT_DATA_LIST = "timelineScreenshotDataList";

    @JsonProperty(ATTR_GROUP_ORDER)
    public final int groupOrder;

    @JsonProperty(API.PARAM_GROUP_NAME)
    public final String groupName;

    @JsonProperty(ATTR_TIMELINE_SCREENSHOT_DATA_LIST)
    public final List<TimelineScreenshotData> timelineScreenshotDataList;

    public TimelineGroupData(
            @JsonProperty(ATTR_GROUP_ORDER) int groupOrder,
            @JsonProperty(API.PARAM_GROUP_NAME) String groupName,
            @JsonProperty(ATTR_TIMELINE_SCREENSHOT_DATA_LIST) List<TimelineScreenshotData> timelineScreenshotDataList) {

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
