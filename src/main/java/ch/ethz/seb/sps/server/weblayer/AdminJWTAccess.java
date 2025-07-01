/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import ch.ethz.seb.sps.server.weblayer.oauth.authserver.AutoLoginService;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.ethz.seb.sps.domain.api.API;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@SecurityRequirement(name = WebConfig.SWAGGER_AUTH_ADMIN_API)
public class AdminJWTAccess {
    
    private final AutoLoginService autoLoginService;
    

    public AdminJWTAccess(final AutoLoginService autoLoginService) {
        this.autoLoginService = autoLoginService;
    }

    @RequestMapping(
            path = API.OAUTH_JWTTOKEN_ENDPOINT,
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String getJWTToken(
            @RequestParam(required = true) final String username,
            @RequestParam(required = true) final String password,
            @RequestParam(required = false) final String redirect) {

        return autoLoginService.createAutoLoginToken(username, password, redirect);
    }

    @RequestMapping(
            path = API.OAUTH_JWTTOKEN_VERIFY_ENDPOINT,
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AutoLoginService.LoginInfo verifyJWTToken(@RequestParam(required = true) final String loginToken) {
        return autoLoginService.verifyAutoLoginToken(loginToken);
    }
    
}
