/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.ethz.seb.sps.domain.Domain.USER;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.api.API.UserRole;
import ch.ethz.seb.sps.domain.api.APIError;
import ch.ethz.seb.sps.domain.api.APIErrorException;
import ch.ethz.seb.sps.domain.api.POSTMapper;
import ch.ethz.seb.sps.domain.api.TooManyRequests;
import ch.ethz.seb.sps.domain.model.user.PasswordChange;
import ch.ethz.seb.sps.domain.model.user.UserInfo;
import ch.ethz.seb.sps.domain.model.user.UserMod;
import ch.ethz.seb.sps.server.ServiceConfig;
import ch.ethz.seb.sps.server.datalayer.dao.AuditLogDAO;
import ch.ethz.seb.sps.server.datalayer.dao.UserDAO;
import ch.ethz.seb.sps.server.servicelayer.BeanValidationService;
import io.github.bucket4j.local.LocalBucket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;

@RestController
@RequestMapping(API.REGISTER_ENDPOINT)
public class RegisterUserController {

    private final AuditLogDAO auditLogDAO;
    private final UserDAO userDAO;
    private final BeanValidationService beanValidationService;
    private final LocalBucket requestRateLimitBucket;
    private final LocalBucket createRateLimitBucket;

    protected RegisterUserController(
            final AuditLogDAO auditLogDAO,
            final UserDAO userDAO,
            final BeanValidationService beanValidationService,
            final RateLimitService rateLimitService,
            @Qualifier(ServiceConfig.USER_PASSWORD_ENCODER_BEAN_NAME) final PasswordEncoder userPasswordEncoder) {

        this.auditLogDAO = auditLogDAO;
        this.userDAO = userDAO;
        this.beanValidationService = beanValidationService;

        this.requestRateLimitBucket = rateLimitService.createRequestLimitBucker();
        this.createRateLimitBucket = rateLimitService.createCreationLimitBucker();
    }

    @Operation(
            summary = "Register a new user account",
            description = "",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = { @Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE) }))
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserInfo registerNewUser(
            @RequestParam(name = USER.ATTR_NAME, required = true) final String name,
            @RequestParam(name = USER.ATTR_SURNAME, required = true) final String surname,
            @RequestParam(name = USER.ATTR_USERNAME, required = true) final String username,
            @RequestParam(name = USER.ATTR_EMAIL, required = false) final String email,
            @RequestParam(name = USER.ATTR_LANGUAGE, required = false) final String language,
            @RequestParam(name = USER.ATTR_TIMEZONE, required = false) final String timezone,
            @RequestParam(name = PasswordChange.ATTR_NAME_NEW_PASSWORD, required = true) final String password,
            @RequestParam(name = PasswordChange.ATTR_NAME_CONFIRM_NEW_PASSWORD,
                    required = true) final String retypedPassword,
            final HttpServletRequest request) {

        if (!this.requestRateLimitBucket.tryConsume(1)) {
            throw new TooManyRequests();
        }

        final POSTMapper postMap = new POSTMapper(request.getParameterMap(), request.getQueryString())
                .put(USER.ATTR_ROLES, UserRole.PROCTOR.name())
                .putIfAbsent(USER.ATTR_LANGUAGE, Locale.ENGLISH.toLanguageTag())
                .putIfAbsent(USER.ATTR_TIMEZONE, DateTimeZone.UTC.getID());
        final UserMod userMod = new UserMod(null, postMap);

        return this.beanValidationService.validateBean(userMod)
                .map(userAccount -> {

                    final List<APIError> errors = new ArrayList<>();
                    if (!userAccount.newPasswordMatch()) {
                        errors.add(APIError.fieldValidationError(
                                new FieldError(
                                        "passwordChange",
                                        PasswordChange.ATTR_NAME_CONFIRM_NEW_PASSWORD,
                                        "user:confirmNewPassword:password.mismatch")));
                    }

                    if (!errors.isEmpty()) {
                        final APIError mainError = errors.remove(0);
                        throw new APIErrorException(new APIError(
                                mainError.errorType,
                                mainError.request,
                                mainError.message,
                                mainError.attributes,
                                errors));
                    }

                    if (!this.createRateLimitBucket.tryConsume(1)) {
                        throw new TooManyRequests(TooManyRequests.Code.REGISTRATION);
                    }

                    return userAccount;
                })
                .flatMap(this.userDAO::createNew)
                .flatMap(account -> this.userDAO.setActive(account, true))
                .flatMap(this.auditLogDAO::logRegisterAccount)
                .flatMap(account -> this.userDAO.byModelId(account.getModelId()))
                .getOrThrow();
    }

}
