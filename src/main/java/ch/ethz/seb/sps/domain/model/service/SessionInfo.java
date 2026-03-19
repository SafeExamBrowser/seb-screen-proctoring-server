package ch.ethz.seb.sps.domain.model.service;

import ch.ethz.seb.sps.domain.Domain;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SessionInfo (
        @JsonProperty(Domain.SESSION.ATTR_UUID) String uuid,
        @JsonProperty(Domain.SESSION.ATTR_CLIENT_NAME) String clientName,
        @JsonProperty(Domain.SESSION.ATTR_CLIENT_IP) String clientIP,
        @JsonProperty(Domain.SESSION.ATTR_CLIENT_MACHINE_NAME) String clientMachineName,
        @JsonProperty(Domain.SESSION.ATTR_CLIENT_OS_NAME) String clientOSName,
        @JsonProperty(Domain.SESSION.ATTR_CLIENT_VERSION) String clientVersion,
        @JsonProperty(Domain.SESSION.ATTR_CREATION_TIME) Long creationTime,
        @JsonProperty(Domain.SESSION.ATTR_TERMINATION_TIME) Long terminationTime
) {

    @JsonCreator
    public SessionInfo {}
}
