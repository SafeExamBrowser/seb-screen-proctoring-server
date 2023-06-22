/*
 * Copyright (c) 2018 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.api;

import java.util.EnumSet;

import ch.ethz.seb.sps.domain.model.Entity;

public final class API {

    public enum UserRole {
        ADMIN,
        PROCTOR,
        SEBCLIENT
    }

    public enum PrivilegeType {
        READ("r"),
        MODIFY("m"),
        WRITE("w");

        public final String flag;

        private PrivilegeType(final String flag) {
            this.flag = flag;
        }

    }

    public static final EnumSet<PrivilegeType> PRIVILEGES_WRITE = EnumSet.of(
            PrivilegeType.READ,
            PrivilegeType.MODIFY,
            PrivilegeType.WRITE);

    public static final EnumSet<PrivilegeType> PRIVILEGES_MODIFY = EnumSet.of(
            PrivilegeType.READ,
            PrivilegeType.MODIFY);

    public static final EnumSet<PrivilegeType> PRIVILEGES_READONLY = EnumSet.of(
            PrivilegeType.READ);

    public static final String ACTIVE_FILTER = Entity.FILTER_ATTR_ACTIVE;

    public static final String OAUTH_ENDPOINT = "/oauth";
    public static final String OAUTH_TOKEN_ENDPOINT = OAUTH_ENDPOINT + "/token";
    public static final String OAUTH_REVOKE_TOKEN_ENDPOINT = OAUTH_ENDPOINT + "/revoke-token";
    public static final String HEALTH_ENDPOINT = "/health";
    public static final String WEBSOCKET_SESSION_ENDPOINT = "/wsock";

    public static final String PARAM_MODEL_ID = "modelId";
    public static final String PARAM_MODEL_ID_LIST = "modelIds";
    public static final String PARAM_ENTITY_TYPE = "entityType";
    public static final String PARAM_MODEL_PATH_SEGMENT = "/{modelId}";

    public static final String NAMES_PATH_SEGMENT = "/names";
    public static final String LIST_PATH_SEGMENT = "/list";
    public static final String ACTIVE_PATH_SEGMENT = "/active";
    public static final String TOGGLE_ACTIVITY_PATH_SEGMENT = "/toggle-activity";
    public static final String INACTIVE_PATH_SEGMENT = "/inactive";
    public static final String PATH_VAR_ACTIVE = PARAM_MODEL_PATH_SEGMENT + ACTIVE_PATH_SEGMENT;
    public static final String PATH_VAR_INACTIVE = PARAM_MODEL_PATH_SEGMENT + INACTIVE_PATH_SEGMENT;

    public static final String USER_ACCOUNT_ENDPOINT = "/useraccount";
    public static final String CURRENT_USER_PATH_SEGMENT = "/me";
    public static final String CURRENT_USER_ENDPOINT = API.USER_ACCOUNT_ENDPOINT + CURRENT_USER_PATH_SEGMENT;
    public static final String SELF_PATH_SEGMENT = "/self";
    public static final String LOGIN_PATH_SEGMENT = "/loglogin";
    public static final String LOGOUT_PATH_SEGMENT = "/loglogout";
    public static final String PASSWORD_PATH_SEGMENT = "/password";

    public static final String CLIENT_ACCESS_ENDPOINT = "/clientaccess";

    public static final String ADMIN_SESSION_ENDPOINT = "adminsession";

    public static final String GROUP_HEADER_UUID = "SEB_GROUP_UUID";
    public static final String SESSION_HEADER_UUID = "SEB_SESSION_UUID";
    public static final String SESSION_HEADER_SEB_USER_NAME = "SEB_USER_NAME";
    public static final String SESSION_HEADER_SEB_IP = "SEB_IP_ADDRESS";
    public static final String SESSION_HEADER_SEB_MACHINE_NAME = "SEB_MACHINE_NAME";
    public static final String SESSION_HEADER_SEB_OS = "SEB_OS_NAME";
    public static final String SESSION_HEADER_SEB_VERSION = "SEB_VERSION";

    public static final String SPS_SERVER_HEALTH = "SPS_SERVER_HEALTH";

    public static final String GROUP_ENDPOINT = "/group";
    public static final String SESSION_ENDPOINT = "/session";
    public static final String SESSION_SCREENSHOT_ENDPOINT = "/screenshot";
    public static final String SESSION_SCREENSHOT_LATEST_ENDPOINT = "/latest";

}
