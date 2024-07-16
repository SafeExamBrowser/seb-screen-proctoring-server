package ch.ethz.seb.sps.domain.model.service;

import ch.ethz.seb.sps.domain.Domain;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupSessionCount {

    public static final String ATTR_ACTIVE_COUNT = "activeCount";
    public static final String ATTR_TOTAL_COUNT = "totalCount";

    @JsonProperty(Domain.SEB_GROUP.ATTR_UUID)
    public final String groupUUID;
    @JsonProperty(ATTR_ACTIVE_COUNT)
    public final Integer activeCount;
    @JsonProperty(ATTR_TOTAL_COUNT)
    public final Integer totalCount;

    @JsonCreator
    public GroupSessionCount(
            @JsonProperty(Domain.SEB_GROUP.ATTR_UUID) final String groupUUID,
            @JsonProperty(ATTR_ACTIVE_COUNT) final Integer activeCount,
            @JsonProperty(ATTR_TOTAL_COUNT) final Integer totalCount) {

        this.groupUUID = groupUUID;
        this.activeCount = activeCount;
        this.totalCount = totalCount;
    }

    public String getGroupUUID() {
        return groupUUID;
    }

    public Integer getActiveCount() {
        return activeCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    @Override
    public String toString() {
        return "GroupSessionCount{" +
                "groupUUID='" + groupUUID + '\'' +
                ", activeCount=" + activeCount +
                ", totalCount=" + totalCount +
                '}';
    }
}
