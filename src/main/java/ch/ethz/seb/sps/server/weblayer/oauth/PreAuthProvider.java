/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer.oauth;

import javax.annotation.PostConstruct;

import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.stereotype.Component;

@Component
public class PreAuthProvider extends PreAuthenticatedAuthenticationProvider {

    private final WebServiceUserDetails webServiceUserDetails;

    public PreAuthProvider(final WebServiceUserDetails webServiceUserDetails) {
        this.webServiceUserDetails = webServiceUserDetails;
    }

    @PostConstruct
    public void init() {
        super.setPreAuthenticatedUserDetailsService(this.webServiceUserDetails);
    }
}
