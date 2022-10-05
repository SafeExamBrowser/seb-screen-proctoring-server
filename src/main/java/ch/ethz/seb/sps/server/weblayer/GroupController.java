/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.server.servicelayer.SessionService;

@RestController
@RequestMapping("${sps.api.session.endpoint.v1}" + API.GROUP_ENDPOINT)
public class GroupController {

    private final SessionService sessionService;

    public GroupController(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @RequestMapping(
            path = API.PARAM_MODEL_PATH_SEGMENT + API.SESSION_ENDPOINT,
            method = RequestMethod.GET,
            produces = {
                    MediaType.IMAGE_PNG_VALUE,
                    MediaType.IMAGE_JPEG_VALUE,
                    MediaType.IMAGE_GIF_VALUE,
                    MediaType.APPLICATION_OCTET_STREAM_VALUE })
    public String getActiveSessions(
            @PathVariable(name = API.PARAM_MODEL_ID) final String groupUUID) {

        return this.sessionService
                .getActiveSessions(groupUUID)
                .getOrThrow();
    }

}
