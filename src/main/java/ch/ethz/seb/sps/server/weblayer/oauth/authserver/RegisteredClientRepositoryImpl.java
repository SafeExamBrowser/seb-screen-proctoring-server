/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer.oauth.authserver;

import ch.ethz.seb.sps.server.servicelayer.SEBClientAccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

public class RegisteredClientRepositoryImpl implements RegisteredClientRepository {

    private static final Logger log = LoggerFactory.getLogger(RegisteredClientRepositoryImpl.class);
    
    private final SEBClientAccessService sebClientAccessService;
    private final RegisteredGuiClient registeredGuiClient;
    private final RegisteredSEBServerClient registeredSEBServerClient;

    public RegisteredClientRepositoryImpl(
            final SEBClientAccessService sebClientAccessService,
            final RegisteredGuiClient registeredGuiClient, 
            final RegisteredSEBServerClient registeredSEBServerClient) {
        
        this.sebClientAccessService = sebClientAccessService;
        this.registeredGuiClient = registeredGuiClient;
        this.registeredSEBServerClient = registeredSEBServerClient;
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        throw new UnsupportedOperationException("Not supported here");
    }

    @Override
    public RegisteredClient findById(String id) {
        return findByClientId(id);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        if (clientId == null) {
            return null;
        }

        // check if it is valid GUI client
        if (clientId.equals(this.registeredGuiClient.getClientId())) {
            return this.registeredGuiClient.client;
        }
        
        // check if it is valid SEB Server client
        if (clientId.equals(this.registeredSEBServerClient.getClientId())) {
            return this.registeredSEBServerClient.client;
        }

        // check if it is valid SEB client
        return this.sebClientAccessService
                .getClientDetails(clientId)
                .onError(error -> log.warn("Active client not found: {} cause: {}", clientId, error.getMessage()))
                .getOr(null);
    }
}
