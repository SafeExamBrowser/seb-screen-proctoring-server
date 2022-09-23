/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain;

public interface Domain {

    interface USER {
        String TYPE_NAME = "User";
        String REFERENCE_NAME = "users";
        String ATTR_ID = "id";
        String ATTR_UUID = "uuid";
        String ATTR_CREATION_DATE = "creationDate";
        String ATTR_NAME = "name";
        String ATTR_SURNAME = "surname";
        String ATTR_USERNAME = "username";
        String ATTR_PASSWORD = "password";
        String ATTR_EMAIL = "email";
        String ATTR_LANGUAGE = "language";
        String ATTR_TIMEZONE = "timezone";
        String ATTR_ACTIVE = "active";
        String ATTR_ROLES = "roles";
    }

    interface CLIENT_ACCESS {
        String ATTR_ID = "id";
        String ATTR_CLIENT_ID = "clientId";
        String ATTR_CLIENT_SECRET = "clientSecret";
        String ATTR_CREATION_DATE = "creationDate";
        String ATTR_ACTIVE = "active";
    }

    interface GROUP {
        String ATTR_ID = "id";
        String ATTR_UUID = "uuid";
        String ATTR_NAME = "name";
    }

    interface SESSION {
        String ATTR_ID = "id";
        String ATTR_GROUP_ID = "groupId";
        String ATTR_UUID = "uuid";
        String ATTR_NAME = "name";
    }

    interface SCREENSHOT_DATA {
        String ATTR_ID = "id";
        String ATTR_GROUP_ID = "groupId";
        String ATTR_SESSION_UUID = "sessionUUID";
        String ATTR_TIMESTAMP = "timestamp";
        String ATTR_IMAGE_URL = "imageURL";
        String ATTR_IMAGE_FORMAT = "imageFormat";
        String ATTR_META_DATA = "metaData";
    }

    interface SCREENSHOT {
        String ATTR_ID = "id";
        String ATTR_IMAGE = "image";
    }

}
