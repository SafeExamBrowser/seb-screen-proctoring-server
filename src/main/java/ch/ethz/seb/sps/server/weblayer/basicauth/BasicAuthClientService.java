/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer.basicauth;

import ch.ethz.seb.sps.server.weblayer.oauth.authserver.RegisteredGuiClient;
import ch.ethz.seb.sps.server.weblayer.oauth.authserver.RegisteredSEBServerClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class BasicAuthClientService implements UserDetailsService {

    private final RegisteredGuiClient registeredGuiClient;
    private final RegisteredSEBServerClient registeredSEBServerClient;

    public BasicAuthClientService(
            RegisteredGuiClient registeredGuiClient, 
            RegisteredSEBServerClient registeredSEBServerClient) {
        
        this.registeredGuiClient = registeredGuiClient;
        this.registeredSEBServerClient = registeredSEBServerClient;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null) {
            throw new UsernameNotFoundException("Missing username");
        }
        
        if (registeredGuiClient.getClientId().endsWith(username)) {
            return registeredGuiClient.getUserDetails();
        }

        if (registeredSEBServerClient.getClientId().endsWith(username)) {
            return registeredSEBServerClient.getUserDetails();
        }

        throw new UsernameNotFoundException("Unknown User: " + username);
    }
}
