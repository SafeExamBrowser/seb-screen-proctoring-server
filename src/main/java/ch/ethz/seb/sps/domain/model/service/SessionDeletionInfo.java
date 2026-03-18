package ch.ethz.seb.sps.domain.model.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SessionDeletionInfo(
        @JsonProperty(ATT_SEARCH_NAME) String searchName,
        @JsonProperty("session") Session session,
        @JsonProperty("groupName") String groupName,
        @JsonProperty("examName") String examName,
        @JsonProperty("examUUID") String examUUID,
        @JsonProperty("institutionId") Long institutionId,
        @JsonProperty("numberOfScreenshots") Long numberOfScreenshots
) {

    public static final String ATT_SEARCH_NAME = "searchName";

    @JsonCreator
    public SessionDeletionInfo {}
}
