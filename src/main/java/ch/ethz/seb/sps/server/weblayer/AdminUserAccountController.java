/*
 * Copyright (c) 2018 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.validation.Valid;

import org.mybatis.dynamic.sql.SqlTable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.api.API.PrivilegeType;
import ch.ethz.seb.sps.domain.api.API.UserRole;
import ch.ethz.seb.sps.domain.api.APIError;
import ch.ethz.seb.sps.domain.api.APIErrorException;
import ch.ethz.seb.sps.domain.api.POSTMapper;
import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.user.AuditLog;
import ch.ethz.seb.sps.domain.model.user.EntityPrivilege;
import ch.ethz.seb.sps.domain.model.user.PasswordChange;
import ch.ethz.seb.sps.domain.model.user.ServerUser;
import ch.ethz.seb.sps.domain.model.user.UserAccount;
import ch.ethz.seb.sps.domain.model.user.UserInfo;
import ch.ethz.seb.sps.domain.model.user.UserMod;
import ch.ethz.seb.sps.server.ServiceConfig;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.UserRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.dao.AuditLogDAO;
import ch.ethz.seb.sps.server.datalayer.dao.EntityPrivilegeDAO;
import ch.ethz.seb.sps.server.datalayer.dao.UserDAO;
import ch.ethz.seb.sps.server.servicelayer.BeanValidationService;
import ch.ethz.seb.sps.server.servicelayer.PaginationService;
import ch.ethz.seb.sps.server.servicelayer.UserService;
import ch.ethz.seb.sps.server.weblayer.oauth.RevokeTokenEndpoint;
import ch.ethz.seb.sps.utils.Result;

@RestController
@RequestMapping("${sps.api.admin.endpoint.v1}" + API.USER_ACCOUNT_ENDPOINT)
public class AdminUserAccountController extends ActivatableEntityController<UserInfo, UserMod> {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserDAO userDAO;
    private final PasswordEncoder userPasswordEncoder;
    private final EntityPrivilegeDAO entityPrivilegeDAO;

    public AdminUserAccountController(
            final UserDAO userDAO,
            final AuditLogDAO auditLogDAO,
            final EntityPrivilegeDAO entityPrivilegeDAO,
            final UserService userService,
            final PaginationService paginationService,
            final ApplicationEventPublisher applicationEventPublisher,
            final BeanValidationService beanValidationService,
            @Qualifier(ServiceConfig.USER_PASSWORD_ENCODER_BEAN_NAME) final PasswordEncoder userPasswordEncoder) {

        super(userService, userDAO, auditLogDAO, paginationService, beanValidationService);
        this.entityPrivilegeDAO = entityPrivilegeDAO;
        this.applicationEventPublisher = applicationEventPublisher;
        this.userDAO = userDAO;
        this.userPasswordEncoder = userPasswordEncoder;
    }

    @RequestMapping(path = API.CURRENT_USER_PATH_SEGMENT, method = RequestMethod.GET)
    public UserInfo loggedInUser() {
        return this.userService
                .getCurrentUser()
                .getUserInfo();
    }

    @RequestMapping(path = API.LOGIN_PATH_SEGMENT, method = RequestMethod.POST)
    public void logLogin() {
        this.auditLogDAO.logLogin(this.userService
                .getCurrentUser()
                .getUserInfo());
    }

    @RequestMapping(path = API.LOGOUT_PATH_SEGMENT, method = RequestMethod.POST)
    public void logLogout() {
        this.auditLogDAO.logLogout(this.userService
                .getCurrentUser()
                .getUserInfo());
    }

    @RequestMapping(
            path = API.PASSWORD_PATH_SEGMENT,
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserInfo changePassword(@Valid @RequestBody final PasswordChange passwordChange) {

        final String modelId = passwordChange.getModelId();
        return this.userDAO.byModelId(modelId)
                .map(this.userService::checkModify)
                .map(ui -> checkPasswordChange(ui, passwordChange))
                .flatMap(e -> this.userDAO.changePassword(modelId, passwordChange.getNewPassword()))
                .flatMap(this::revokeAccessToken)
                .flatMap(e -> this.auditLogDAO.log(AuditLog.AuditLogType.PASSWORD_CHANGE, e))
                .getOrThrow();
    }

    @Override
    protected SqlTable getSQLTableOfEntity() {
        return UserRecordDynamicSqlSupport.userRecord;
    }

    @Override
    protected UserMod createNew(final POSTMapper postParams) {
        return new UserMod(null, postParams);
    }

    @Override
    protected UserInfo merge(final UserMod modifyData, final UserInfo existingEntity) {
        return new UserInfo(
                existingEntity.uuid,
                modifyData.name,
                modifyData.surname,
                modifyData.username,
                modifyData.email,
                modifyData.language,
                modifyData.timeZone,
                modifyData.roles,
                null, null, null);
    }

    @Override
    protected Result<UserMod> validForCreate(final UserMod userInfo) {
        return super.validForCreate(userInfo)
                .flatMap(this::passwordMatch);
    }

    private Result<UserInfo> revokeAccessToken(final UserInfo userInfo) {
        return Result.tryCatch(() -> {
            this.applicationEventPublisher.publishEvent(
                    new RevokeTokenEndpoint.RevokeTokenEvent(userInfo, userInfo.username));
            return userInfo;
        });
    }

    private <T extends UserAccount> Result<UserMod> passwordMatch(final UserMod userInfo) {
        if (!userInfo.newPasswordMatch()) {
            throw APIErrorException.ofFieldValidation(
                    "passwordChange",
                    PasswordChange.ATTR_NAME_CONFIRM_NEW_PASSWORD,
                    "user:confirmNewPassword:password.mismatch");
        }

        return Result.of(userInfo);
    }

    private UserInfo checkPasswordChange(final UserInfo info, final PasswordChange passwordChange) {
        final ServerUser currentUser = this.userDAO.byUsername(this.userService
                .getCurrentUser().getUsername())
                .getOrThrow();

        final Collection<APIError> errors = new ArrayList<>();

        if (!this.userPasswordEncoder.matches(passwordChange.getPassword(), currentUser.getPassword())) {

            errors.add(APIError.fieldValidationError(
                    new FieldError(
                            "passwordChange",
                            PasswordChange.ATTR_NAME_PASSWORD,
                            "user:password:password.wrong")));
        }

        if (!passwordChange.newPasswordMatch()) {

            errors.add(APIError.fieldValidationError(
                    new FieldError(
                            "passwordChange",
                            PasswordChange.ATTR_NAME_CONFIRM_NEW_PASSWORD,
                            "user:confirmNewPassword:password.mismatch")));
        }

        if (!errors.isEmpty()) {
            throw APIErrorException.ofFieldValidation("passwordChange", errors);
        }

        return info;

    }

    @RequestMapping(
            path = API.ENTITY_PRIVILEGE_ENDPOINT,
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityPrivilege createEntityPrivilege(
            @RequestParam(name = Domain.ENTITY_PRIVILEGE.ATTR_ENTITY_TYPE, required = true) final String entityType,
            @RequestParam(name = Domain.ENTITY_PRIVILEGE.ATTR_ENTITY_ID, required = true) final Long entityId,
            @RequestParam(name = Domain.ENTITY_PRIVILEGE.ATTR_USER_UUID, required = true) final String userUUID,
            @RequestParam(name = Domain.ENTITY_PRIVILEGE.ATTR_PRIVILEGES, required = true) final String privilege) {

        checkAdminRole();

        return this.entityPrivilegeDAO.addPrivilege(
                EntityType.valueOf(entityType),
                entityId,
                userUUID,
                PrivilegeType.valueOf(privilege))
                .getOrThrow();
    }

    @RequestMapping(
            path = API.ENTITY_PRIVILEGE_ENDPOINT,
            method = RequestMethod.DELETE,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityKey deleteEntityPrivilege(
            @RequestParam(name = Domain.ENTITY_PRIVILEGE.ATTR_ENTITY_TYPE, required = true) final String entityType,
            @RequestParam(name = Domain.ENTITY_PRIVILEGE.ATTR_ENTITY_ID, required = true) final Long entityId,
            @RequestParam(name = Domain.ENTITY_PRIVILEGE.ATTR_USER_UUID, required = true) final String userUUID) {

        checkAdminRole();

        return this.entityPrivilegeDAO.deletePrivilege(
                EntityType.valueOf(entityType),
                entityId,
                userUUID)
                .getOrThrow();
    }

    private void checkAdminRole() {
        final Set<UserRole> userRoles = this.userService.getCurrentUser().getUserRoles();
        if (!userRoles.contains(UserRole.ADMIN)) {
            throw APIErrorException.ofPermissionDenied(
                    EntityType.ENTITY_PRIVILEGE,
                    PrivilegeType.WRITE,
                    this.userService.getCurrentUser().getUserInfo());
        }
    }

}
