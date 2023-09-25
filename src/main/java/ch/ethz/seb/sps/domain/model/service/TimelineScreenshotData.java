package ch.ethz.seb.sps.domain.model.service;

import java.util.Map;

public class TimelineScreenshotData {

    public final Long timestamp;
    public final Map<String, String> metaData;


    public TimelineScreenshotData(Long timestamp, Map<String, String> metaData) {
        this.timestamp = timestamp;
        this.metaData = metaData;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }
}
