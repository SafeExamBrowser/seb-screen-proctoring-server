package ch.ethz.seb.sps.domain.model.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SessionDeletionInfo(
        @JsonProperty(ATT_SEARCH_NAME) String searchName,
        @JsonProperty("session") SessionInfo sessionInfo,
        @JsonProperty("groupName") String groupName,
        @JsonProperty("examName") String examName,
        @JsonProperty("examUUID") String examUUID,
        @JsonProperty("institutionId") Long institutionId,
        @JsonProperty("numberOfScreenshots") Long numberOfScreenshots,
        @JsonProperty("error") String error
) {

    public static final String ATT_SEARCH_NAME = "searchName";

    @JsonCreator
    public SessionDeletionInfo {}

    public SessionDeletionInfo withError(final Exception e) {
        if (e == null) {
            return this;
        }

        return new SessionDeletionInfo(
                this.searchName,
                this.sessionInfo,
                this.groupName,
                this.examName,
                this.examUUID,
                this.institutionId,
                this.numberOfScreenshots,
                e.getMessage()
        );
    }
}
