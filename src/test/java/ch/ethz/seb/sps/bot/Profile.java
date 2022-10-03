/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.bot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Profile {

    @JsonProperty("webserviceAddress")
    String webserviceAddress = "http://localhost:8080";
    @JsonProperty("websocketAddress")
    String websocketAddress = "ws://localhost:8080";
    @JsonProperty("apiPath")
    String apiPath = "/session-api/v1";
    @JsonProperty("accessTokenEndpoint")
    String accessTokenEndpoint = "/oauth/token";

    @JsonProperty("groupId")
    String groupId = "test-group";
    @JsonProperty("sessionId")
    String sessionId = null;
    @JsonProperty("clientId")
    String clientId = "test";
    @JsonProperty("clientSecret")
    String clientSecret = "test";

    @JsonProperty("runtime")
    int runtime = 60000;
    @JsonProperty("numberOfConnections")
    int numberOfConnections = 1;
    @JsonProperty("spawnDelay")
    long spawnDelay = 200;
    @JsonProperty("screenshotIntervall")
    long screenshotInterval = 1000;

}