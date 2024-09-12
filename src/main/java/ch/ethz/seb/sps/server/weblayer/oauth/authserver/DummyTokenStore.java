/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer.oauth.authserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.server.authorization.*;

public class DummyTokenStore implements OAuth2AuthorizationService  {

    private static final Logger logger = LoggerFactory.getLogger(DummyTokenStore.class);
    
    public void save(OAuth2Authorization authorization) {
        if (authorization == null) {
            return;
        }
        logger.info("New token for: {}", authorization.getPrincipalName());
    }

    public void remove(OAuth2Authorization authorization) {
        
    }
    
    public OAuth2Authorization findById(String id) {
        return null;
    }
    
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        return null;
    }
}
