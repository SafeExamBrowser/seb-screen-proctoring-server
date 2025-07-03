/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.server.ServiceInfo;
import ch.ethz.seb.sps.server.servicelayer.SessionServiceHealthControl;

@RestController
public class HealthController {

    private final SessionServiceHealthControl sessionServiceHealthControl;
    private final ServiceInfo serviceInfo;

    public HealthController(
            final SessionServiceHealthControl sessionServiceHealthControl,
            final ServiceInfo serviceInfo) {

        this.sessionServiceHealthControl = sessionServiceHealthControl;
        this.serviceInfo = serviceInfo;
    }

    @RequestMapping(
            path = API.HEALTH_ENDPOINT,
            method = RequestMethod.GET)
    public void getServerHealth(final HttpServletResponse response) {

        response.setStatus(HttpStatus.OK.value());
        response.setHeader(
                API.SPS_SERVER_HEALTH,
                String.valueOf(this.sessionServiceHealthControl.getOverallLoadIndicator()));
        response.setHeader(HttpHeaders.CONNECTION, "close");

    }

    @RequestMapping(path = API.GUI_REDIRECT_ENDPOINT, method = RequestMethod.GET)
    public String guiRedirectURL() {
        System.out.println("******************** getGuiRedirectURL: " + this.serviceInfo.getGuiRedirectURL());
        return this.serviceInfo.getGuiRedirectURL();
    }

}
