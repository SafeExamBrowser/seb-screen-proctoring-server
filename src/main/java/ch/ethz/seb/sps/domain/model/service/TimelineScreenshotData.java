package ch.ethz.seb.sps.domain.model.service;

import ch.ethz.seb.sps.domain.Domain;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class TimelineScreenshotData {

    @JsonProperty(Domain.SCREENSHOT_DATA.ATTR_TIMESTAMP)
    public final Long timestamp;

    @JsonProperty(Domain.SCREENSHOT_DATA.ATTR_META_DATA)
    public final Map<String, String> metaData;


    public TimelineScreenshotData(
            @JsonProperty(Domain.SCREENSHOT_DATA.ATTR_TIMESTAMP) Long timestamp,
            @JsonProperty(Domain.SCREENSHOT_DATA.ATTR_META_DATA) Map<String, String> metaData) {

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
