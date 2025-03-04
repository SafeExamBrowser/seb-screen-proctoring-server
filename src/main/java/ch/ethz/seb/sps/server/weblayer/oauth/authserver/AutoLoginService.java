/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer.oauth.authserver;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.user.ServerUser;
import ch.ethz.seb.sps.server.datalayer.dao.UserDAO;
import ch.ethz.seb.sps.server.weblayer.BadRequestException;
import ch.ethz.seb.sps.server.weblayer.oauth.authserver.pwdgrant.OAuth2PasswordGrantAuthenticationProvider;
import ch.ethz.seb.sps.server.weblayer.oauth.authserver.pwdgrant.OAuth2PasswordGrantAuthenticationToken;
import ch.ethz.seb.sps.utils.Constants;
import ch.ethz.seb.sps.utils.Cryptor;
import ch.ethz.seb.sps.utils.Utils;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContext;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class AutoLoginService {

    private static final Logger log = LoggerFactory.getLogger(AutoLoginService.class);

    private static final String SUBJECT_CLAIM_NAME = "sub";
    private static final String PASSWORD_CLAIM = "pwd";
    private static final String USERNAME_CLAIM = "usr";
    private static final String REDIRECT_CLAIM = "redirect";
    
    private final RegisteredGuiClient registeredGuiClient;
    private final UserDAO userDAO;
    private final PasswordEncoder userPasswordEncoder;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final String subjectClaim;
    private final String sebserverclientId;
    
    private OAuth2PasswordGrantAuthenticationProvider oAuth2PasswordGrantAuthenticationProvider = null;

    public AutoLoginService(
            final RegisteredGuiClient registeredGuiClient,
            final UserDAO userDAO,
            final PasswordEncoder userPasswordEncoder,
            final JwtEncoder jwtEncoder,
            final JwtDecoder jwtDecoder, Cryptor cryptor,
            @Value("${sps.api.admin.sebserver.clientId}") final String sebserverclientId) {
        
        this.registeredGuiClient = registeredGuiClient;
        this.userDAO = userDAO;
        this.userPasswordEncoder = userPasswordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.subjectClaim = "logintoken_" + Utils.hash_SHA_256_Base_16(cryptor.getInternalPWD());
        this.sebserverclientId = sebserverclientId;
    }

    void init(OAuth2PasswordGrantAuthenticationProvider oAuth2PasswordGrantAuthenticationProvider) {
        this.oAuth2PasswordGrantAuthenticationProvider = oAuth2PasswordGrantAuthenticationProvider;
    }
    
    public String createAutoLoginToken(String username, String password, String redirect) {
        // first check if requested user exists
        final ServerUser user = this.userDAO
                .byUsername(username)
                .getOrThrow(error -> new BadCredentialsException("wrong username or password", error));

        // check given password matches
        if (!this.userPasswordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("wrong username or password");
        }

        // create JWT for automated user login
        try {

            final Map<String, Object> claims = new HashMap<>();
            claims.put(USERNAME_CLAIM, username);
            claims.put(PASSWORD_CLAIM, password);
            claims.put(REDIRECT_CLAIM, redirect != null ? redirect : StringUtils.EMPTY);

            return createToken(claims, subjectClaim);

        } catch (final Exception e) {
            log.error("Failed to create JWT for autologin for user: {} cause: {}", username, e.getMessage());
            throw new RuntimeException("Unexpected error while trying to generate login JWT: ", e);
        }
    }

    public LoginInfo verifyAutoLoginToken(String logintoken) {

        final Jwt claims = checkJWTValid(logintoken);
        final String username = claims.getClaimAsString(USERNAME_CLAIM);
        final String password = claims.getClaimAsString(PASSWORD_CLAIM);
        final String redirect = claims.getClaimAsString(REDIRECT_CLAIM);
        
        // verify user
        final ServerUser user = this.userDAO
                .byUsername(username)
                .getOrThrow(error -> new BadCredentialsException("Unknown user claim", error));

        if (!userPasswordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Unknown user");
        }

        // generate access and refresh tokens
        final Map<String, Object> params = new HashMap<>();
        params.put("grant_type", "password");
        params.put("username", username);
        params.put("password", password);
        OAuth2ClientAuthenticationToken oAuth2ClientAuthenticationToken = new OAuth2ClientAuthenticationToken(
                registeredGuiClient.client,
                ClientAuthenticationMethod.CLIENT_SECRET_POST,
                null);

        OAuth2PasswordGrantAuthenticationToken oAuth2PasswordGrantAuthenticationToken = new OAuth2PasswordGrantAuthenticationToken(
                oAuth2ClientAuthenticationToken,
                registeredGuiClient.client.getScopes(),
                params);


        AuthorizationServerContextHolder.setContext(
                new AutologinAuthorizationServerContext(AuthorizationServerSettings
                .builder()
                .build(), this.sebserverclientId));

        OAuth2AccessTokenAuthenticationToken tokenInfo = (OAuth2AccessTokenAuthenticationToken) this.oAuth2PasswordGrantAuthenticationProvider
                .authenticate(oAuth2PasswordGrantAuthenticationToken);

        return new LoginInfo(
                username, 
                user.uuid(), 
                redirect, 
                new TokenResponse(
                        tokenInfo.getAccessToken().getTokenValue(),
                        tokenInfo.getRefreshToken().getTokenValue(),
                        tokenInfo.getAccessToken().getScopes().toString(),
                        tokenInfo.getAccessToken().getTokenType().getValue(),
                        tokenInfo.getAccessToken().getExpiresAt().getEpochSecond()
                ));
    }

    // NOTE Token is expired in one minute
    private String createToken(final Map<String, Object> claims, final String subject) {
        final long millisecondsNow = Utils.getMillisecondsNow();

    
        JwtEncoderParameters params = JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(),
                JwtClaimsSet
                .builder()
                .claims(c -> c.putAll(claims))
                .subject(subject)
                .issuedAt(new Date(millisecondsNow).toInstant())
                .expiresAt(new Date(millisecondsNow + Constants.MINUTE_IN_MILLIS).toInstant())
                .build());

       
        return jwtEncoder.encode(params).getTokenValue();
    }

    private Jwt checkJWTValid(final String logintoken) {

        try {
            Jwt decodedJWT = jwtDecoder.decode(logintoken);

            // check expiration date
            final long now = Utils.getSecondsNow();
            Instant expiresAt = decodedJWT.getExpiresAt();
            if (expiresAt == null || expiresAt.getEpochSecond() < now) {
                throw new BadRequestException(API.OAUTH_JWTTOKEN_VERIFY_ENDPOINT, "Token expired");
            }

            // check subject
            String subject = decodedJWT.getSubject();
            if (!subjectClaim.equals(subject)) {
                throw new BadRequestException(API.OAUTH_JWTTOKEN_VERIFY_ENDPOINT, "Token subject mismatch");
            }

            return decodedJWT;
        } catch (JwtException je) {
            throw new BadRequestException(API.OAUTH_JWTTOKEN_VERIFY_ENDPOINT, "Invalid JWT");
        } catch (Exception e) {
            log.error("Failed to decode JWT due to unknown error: ", e);
            throw e;
        }
    }

    public final static class LoginInfo {

        @JsonProperty
        public final String username;
        @JsonProperty
        public final String userUUID;
        @JsonProperty
        public final String redirect;
        @JsonProperty
        public final TokenResponse login;

        public LoginInfo(
                final String username,
                final String userUUID,
                final String redirect,
                final TokenResponse login) {

            this.username = username;
            this.userUUID = userUUID;
            this.redirect = redirect;
            this.login = login;
        }
    }

    public final static class TokenResponse {
        @JsonProperty
        public final String access_token;
        @JsonProperty
        public final String refresh_token;
        @JsonProperty
        public final String scope;
        @JsonProperty
        public final String token_type;
        @JsonProperty
        public final Long expires_in;

        public TokenResponse(String access_token, String refresh_token, String scope, String token_type, Long expires_in) {
            this.access_token = access_token;
            this.refresh_token = refresh_token;
            this.scope = scope;
            this.token_type = token_type;
            this.expires_in = expires_in;
        }
    }

    private static final class AutologinAuthorizationServerContext implements AuthorizationServerContext {
        private final AuthorizationServerSettings authorizationServerSettings;
        private final String sebserverclientId;

        private AutologinAuthorizationServerContext(
                AuthorizationServerSettings authorizationServerSettings, 
                String sebserverclientId) {
            
            this.authorizationServerSettings = authorizationServerSettings;
            this.sebserverclientId = sebserverclientId;
        }

        public String getIssuer() {
            return sebserverclientId;
        }

        public AuthorizationServerSettings getAuthorizationServerSettings() {
            return this.authorizationServerSettings;
        }
    }
}
