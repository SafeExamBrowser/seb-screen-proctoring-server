package ch.ethz.seb.sps.domain.model.service;

import java.util.Map;

public class TimelineGroupData {

    public String groupIdentifier;
    public String groupName;
    public Long timestamp;
    public Map<String, String> metaData;

    public TimelineGroupData(){};

    public TimelineGroupData(String groupIdentifier, String groupName, Long timestamp, Map<String, String> metaData) {
        this.groupIdentifier = groupIdentifier;
        this.groupName = groupName;
        this.timestamp = timestamp;
        this.metaData = metaData;
    }

    public String getGroupIdentifier() {
        return groupIdentifier;
    }

    public String getGroupName() {
        return groupName;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }

    public void setGroupIdentifier(String groupIdentifier) {
        this.groupIdentifier = groupIdentifier;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setMetaData(Map<String, String> metaData) {
        this.metaData = metaData;
    }
}
