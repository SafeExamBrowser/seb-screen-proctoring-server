/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

//package ch.ethz.seb.sps.server.weblayer.oauth;
//
//import java.util.Collection;
//
//import jakarta.servlet.http.HttpServletRequest;
//
//import org.apache.http.HttpHeaders;
//import org.springframework.context.ApplicationEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseStatus;
//
//import ch.ethz.seb.sps.domain.api.API;
//
///** Spring MVC controller that defines a revoke token endpoint */
//@Controller
//public class RevokeTokenEndpoint {
//
//    private final ConsumerTokenServices tokenServices;
//    private final GUIClientAPIClientDetails guiClientAPIClientDetails;
//    private final TokenStore tokenStore;
//
//    public RevokeTokenEndpoint(
//            final ConsumerTokenServices tokenServices,
//            final GUIClientAPIClientDetails guiClientAPIClientDetails,
//            final TokenStore tokenStore) {
//
//        this.tokenServices = tokenServices;
//        this.guiClientAPIClientDetails = guiClientAPIClientDetails;
//        this.tokenStore = tokenStore;
//    }
//
//    @RequestMapping(value = API.OAUTH_REVOKE_TOKEN_ENDPOINT, method = RequestMethod.DELETE)
//    @ResponseStatus(HttpStatus.OK)
//    public void logout(final HttpServletRequest request) {
//        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//        if (authHeader != null) {
//            final String tokenId = authHeader.substring("Bearer".length() + 1);
//            this.tokenServices.revokeToken(tokenId);
//        }
//    }
//
//    @EventListener(RevokeTokenEvent.class)
//    void revokeAccessToken(final RevokeTokenEvent event) {
//        final String clientId = this.guiClientAPIClientDetails.getClientId();
//        final Collection<OAuth2AccessToken> tokens = this.tokenStore
//                .findTokensByClientIdAndUserName(clientId, event.userName);
//
//        if (tokens != null) {
//            for (final OAuth2AccessToken token : tokens) {
//                this.tokenStore.removeAccessToken(token);
//            }
//        }
//    }
//
//    @EventListener(RevokeExamTokenEvent.class)
//    void revokeExamAccessToken(final RevokeExamTokenEvent event) {
//        final Collection<OAuth2AccessToken> tokens = this.tokenStore
//                .findTokensByClientId(event.clientId);
//
//        if (tokens != null) {
//            for (final OAuth2AccessToken token : tokens) {
//                this.tokenStore.removeAccessToken(token);
//            }
//        }
//    }
//
//    public static final class RevokeTokenEvent extends ApplicationEvent {
//
//        private static final long serialVersionUID = 5776699085388043743L;
//
//        public final String userName;
//
//        public RevokeTokenEvent(final Object source, final String userName) {
//            super(source);
//            this.userName = userName;
//        }
//
//    }
//
//    public static final class RevokeExamTokenEvent extends ApplicationEvent {
//
//        private static final long serialVersionUID = 5776699085388043743L;
//
//        public final String clientId;
//
//        public RevokeExamTokenEvent(final String clientId) {
//            super(clientId);
//            this.clientId = clientId;
//        }
//
//    }
//
//}
