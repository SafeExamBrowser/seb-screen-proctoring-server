/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.user.ServerUser;
import ch.ethz.seb.sps.server.ServiceConfig;
import ch.ethz.seb.sps.server.datalayer.dao.UserDAO;
import ch.ethz.seb.sps.server.weblayer.oauth.BasicAuthUserDetailService;
import ch.ethz.seb.sps.utils.Constants;
import ch.ethz.seb.sps.utils.Cryptor;
import ch.ethz.seb.sps.utils.Utils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@SecurityRequirement(name = WebServiceConfig.SWAGGER_AUTH_SEBSEVER_ADMIN)
public class AdminJWTAccess {

    private static final String SUBJECT_CLAIM_NAME = "sub";
    private static final String PASSWORD_CLAIM = "pwd";
    private static final String USERNAME_CLAIM = "usr";
    private static final String REDIRECT_CLAIM = "redirect";
    private static final String SUBJECT_CLAIM = "logintoken_" + UUID.randomUUID().toString();

    @Autowired
    @Qualifier(ServiceConfig.USER_PASSWORD_ENCODER_BEAN_NAME)
    private PasswordEncoder userPasswordEncoder;

    private final UserDAO userDAO;
    private final TokenEndpoint tokenEndpoint;
    private final BasicAuthUserDetailService basicAuthUserDetailService;
    private final Cryptor cyptor;

    public AdminJWTAccess(
            final UserDAO userDAO,
            final TokenEndpoint tokenEndpoint,
            final BasicAuthUserDetailService basicAuthUserDetailService,
            final Cryptor cyptor) {

        this.userDAO = userDAO;
        this.tokenEndpoint = tokenEndpoint;
        this.basicAuthUserDetailService = basicAuthUserDetailService;
        this.cyptor = cyptor;
    }

    @RequestMapping(
            path = API.OAUTH_JWTTOKEN_ENDPOINT,
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String getJWTToken(
            @RequestParam(required = true) final String username,
            @RequestParam(required = true) final String password,
            @RequestParam(required = false) final String redirect) {

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

            return createToken(claims, SUBJECT_CLAIM);

        } catch (final Exception e) {
            throw new RuntimeException("Unexpected error while trying to generate login JWT: ", e);
        }
    }

    @RequestMapping(
            path = API.OAUTH_JWTTOKEN_VERIFY_ENDPOINT,
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginInfo verifyJWTToken(@RequestParam(required = true) final String logintoken) {

        try {

            final Claims claims = checkJWTValid(logintoken);
            final String username = claims.get(USERNAME_CLAIM, String.class);
            final String password = claims.get(PASSWORD_CLAIM, String.class);
            final String redirect = claims.get(REDIRECT_CLAIM, String.class);

            // check if requested user exists
            final ServerUser user = this.userDAO
                    .byUsername(username)
                    .getOrThrow(error -> new BadCredentialsException("Unknown user claim", error));

            // login the user by getting access token
            final Map<String, String> params = new HashMap<>();
            params.put("grant_type", "password");
            params.put("username", username);
            params.put("password", password);
            final WebAuthenticationDetails details = new WebAuthenticationDetails("localhost", null);
            final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            this.basicAuthUserDetailService.getGuiUserDetails(),
                            details,
                            Collections.emptyList());
            final ResponseEntity<OAuth2AccessToken> accessToken =
                    this.tokenEndpoint.postAccessToken(usernamePasswordAuthenticationToken, params);
            final OAuth2AccessToken token = accessToken.getBody();

            final LoginInfo loginInfo = new LoginInfo(username, user.uuid(), redirect, token);
            return loginInfo;
        } catch (final BadRequestException bre) {
            throw bre;
        } catch (final Exception e) {
            throw new RuntimeException("Unexpected error while trying to verify login JWT: ", e);
        }
    }

    private Claims checkJWTValid(final String logintoken) {
        // decode given JWT
        final Claims claims = Jwts.parser()
                .setSigningKey(this.cyptor.getInternalPWD().toString())
                .parseClaimsJws(logintoken)
                .getBody();

        // check expiration date
        final long expirationTime = claims.getExpiration().getTime();
        final long now = Utils.getMillisecondsNow();
        if (expirationTime < now) {
            throw new BadRequestException(API.OAUTH_JWTTOKEN_VERIFY_ENDPOINT, "Token expired");
        }

        // check subject
        final String subject = claims.get(SUBJECT_CLAIM_NAME, String.class);
        if (!SUBJECT_CLAIM.equals(subject)) {
            throw new BadRequestException(API.OAUTH_JWTTOKEN_VERIFY_ENDPOINT, "Token subject mismatch");
        }
        return claims;
    }

    // NOTE Token is expires in one minute and is signed with internal secret
    private String createToken(final Map<String, Object> claims, final String subject) {
        final long millisecondsNow = Utils.getMillisecondsNow();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(millisecondsNow))
                .setExpiration(new Date(millisecondsNow + Constants.MINUTE_IN_MILLIS))
                .signWith(SignatureAlgorithm.HS256, this.cyptor.getInternalPWD().toString())
                .compact();
    }

    private final static class LoginInfo {

        @JsonProperty
        public final String username;
        @JsonProperty
        public final String userUUID;
        @JsonProperty
        public final String redirect;
        @JsonProperty
        public final OAuth2AccessToken login;

        public LoginInfo(
                final String username,
                final String userUUID,
                final String redirect,
                final OAuth2AccessToken login) {

            this.username = username;
            this.userUUID = userUUID;
            this.redirect = redirect;
            this.login = login;
        }
    }

}
