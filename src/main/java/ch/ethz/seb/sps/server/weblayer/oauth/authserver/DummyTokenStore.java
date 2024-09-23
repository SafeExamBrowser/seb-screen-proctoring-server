/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer.oauth.authserver;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.server.authorization.*;

public class DummyTokenStore implements OAuth2AuthorizationService  {

    private static final Logger logger = LoggerFactory.getLogger(DummyTokenStore.class);
    
    private final Map<String, OAuth2Authorization> authorizations = new ConcurrentHashMap<>();
    
    public void save(OAuth2Authorization authorization) {
        if (authorization == null) {
            return;
        }
        
        logger.info("New token for: {}", authorization.getPrincipalName());

        if (authorization.getRefreshToken() != null) {
            String token = authorization.getRefreshToken().getToken().getTokenValue();
            authorizations.put(token, authorization);
            cleanup();
        }
    }

    public void remove(OAuth2Authorization authorization) {
        if (authorization != null) {
            OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization.getRefreshToken();
            if (refreshToken != null) {
                authorizations.remove(refreshToken.getToken().getTokenValue());
            }
        }
    }
    
    public OAuth2Authorization findById(String id) {
        return null;
    }
    
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        cleanup();
        if (OAuth2TokenType.REFRESH_TOKEN.equals(tokenType)) {
            return authorizations.get(token);
        }
        return null;
    }

    private void cleanup() {
        Set<String> toRemove = authorizations
                .entrySet()
                .stream()
                .filter(e -> e.getValue().getRefreshToken() == null || e.getValue().getRefreshToken().isExpired())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        if (!toRemove.isEmpty()) {
            toRemove.forEach(authorizations::remove);
        }
    }
}
