/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer.oauth;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class BasicAuthUserDetailService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(BasicAuthUserDetailService.class);

    private final GUIClientAPIClientDetails guiClientAPIClientDetails;
    private final SEBServerAPIClientDetails sebServerAPIClientDetails;

    public BasicAuthUserDetailService(
            final GUIClientAPIClientDetails guiClientAPIClientDetails,
            final SEBServerAPIClientDetails sebServerAPIClientDetails) {

        this.guiClientAPIClientDetails = guiClientAPIClientDetails;
        this.sebServerAPIClientDetails = sebServerAPIClientDetails;
    }

    public UserDetails getGuiUserDetails() {
        return new User(
                this.guiClientAPIClientDetails.getClientId(),
                this.guiClientAPIClientDetails.getClientSecret(),
                Collections.emptyList());
    }

    public UserDetails getSebServerUserDetails() {
        return new User(
                this.sebServerAPIClientDetails.getClientId(),
                this.sebServerAPIClientDetails.getClientSecret(),
                Collections.emptyList());
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        try {

            if (this.guiClientAPIClientDetails.getClientId().equals(username)) {
                return this.getGuiUserDetails();
            }

            if (this.sebServerAPIClientDetails.getClientId().equals(username)) {
                return this.getSebServerUserDetails();
            }

            throw new UsernameNotFoundException(username);

        } catch (final Exception e) {
            log.error("Failed to verify basic auth user by name: {}", username, e);
            throw new UsernameNotFoundException(e.getMessage());
        }
    }

}
