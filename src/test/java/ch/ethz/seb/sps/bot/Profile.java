/*
 * Copyright (c) 2024 ETH Zürich, IT Services
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
    //local
    String webserviceAddress = "http://localhost:8090";
    //ralph
//    String webserviceAddress = "http://ralph.ethz.ch:8090";
    //kubernetes
//    String webserviceAddress = "https://sps-webservice-test.let.ethz.ch";

    @JsonProperty("websocketAddress")
    String websocketAddress =
            "ws://localhost:8090";
    //"ws://ralph.ethz.ch:8090";
    @JsonProperty("apiPath")
    String apiPath = "/seb-api/v1";
    @JsonProperty("accessTokenEndpoint")
    String accessTokenEndpoint = "/oauth/token";

    @JsonProperty("groupId")
    String groupId = "debug test 2";
    @JsonProperty("sessionId")
    String sessionId = null;
    @JsonProperty("clientId")
    String clientId = "test";
    @JsonProperty("clientSecret")
    String clientSecret = "test";

    @JsonProperty("runtime")
//    int runtime = 3 * 60000;
    int runtime = 20 * 1000;

    @JsonProperty("numberOfConnections")
    int numberOfConnections = 1;
    @JsonProperty("spawnDelay")
    long spawnDelay = 1000;
    @JsonProperty("screenshotIntervall")
    long screenshotInterval = 1000;
    @JsonProperty("takeOnlyOneScreenshot")
    boolean takeOnlyOneScreenshot = false;
    @JsonProperty("countConnections")
    boolean countConnections = true;

}