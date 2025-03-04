/*
 * Copyright (c) 2025 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.server.servicelayer.SessionServiceHealthControl;
import ch.ethz.seb.sps.server.servicelayer.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${sps.api.admin.endpoint.v1}" + API.ADMIN_ENDPOINT)
@SecurityRequirement(name = WebConfig.SWAGGER_AUTH_ADMIN_API)
public class AdminController {

    private final UserService userService;
    private final SessionServiceHealthControl sessionServiceHealthControl;

    public AdminController(
            final UserService userService,
            final SessionServiceHealthControl sessionServiceHealthControl) {
        
        this.userService = userService;
        this.sessionServiceHealthControl = sessionServiceHealthControl;
    }

    @RequestMapping(
            path = API.ADMIN_SIMULATE_HEALTH_ENDPOINT + "/{health}",
            method = RequestMethod.POST)
    public void simulateHealth(@PathVariable(name = "health") final int health) {
        userService.checkIsAdmin();
        sessionServiceHealthControl.setSimulateHealthIssue(health);
    }
}
