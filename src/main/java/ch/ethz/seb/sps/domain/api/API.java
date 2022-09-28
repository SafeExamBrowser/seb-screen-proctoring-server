/*
 * Copyright (c) 2018 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.api;

public final class API {

    public enum UserRoles {
        ADMIN,
        PROCTOR,
        SEBCLIENT
    }

    public static final String OAUTH_ENDPOINT = "/oauth";
    public static final String OAUTH_TOKEN_ENDPOINT = OAUTH_ENDPOINT + "/token";
    public static final String OAUTH_REVOKE_TOKEN_ENDPOINT = OAUTH_ENDPOINT + "/revoke-token";

    public static final String PARAM_MODEL_ID = "modelId";
    public static final String PARAM_ENTITY_TYPE = "entityType";
    public static final String PARAM_MODEL_PATH_SEGMENT = "/{modelId}";

    public static final String SESSION_HEADER_UUID = "SEB_SESSION_UUID";

    public static final String SESSION_HANDSHAKE_ENDPOINT = "/handshake";
    public static final String SESSION_SCREENSHOT_ENDPOINT = "/screenshot";
    public static final String SESSION_SCREENSHOT_LATEST_ENDPOINT = "/latest";

}
