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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
import ch.ethz.seb.sps.domain.model.user.UserPrivileges;
import ch.ethz.seb.sps.server.ServiceConfig;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.UserRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.dao.AuditLogDAO;
import ch.ethz.seb.sps.server.datalayer.dao.EntityPrivilegeDAO;
import ch.ethz.seb.sps.server.datalayer.dao.NoResourceFoundException;
import ch.ethz.seb.sps.server.datalayer.dao.UserDAO;
import ch.ethz.seb.sps.server.servicelayer.BeanValidationService;
import ch.ethz.seb.sps.server.servicelayer.EntityService;
import ch.ethz.seb.sps.server.servicelayer.PaginationService;
import ch.ethz.seb.sps.server.servicelayer.UserService;
import ch.ethz.seb.sps.server.weblayer.oauth.RevokeTokenEndpoint;
import ch.ethz.seb.sps.utils.Result;

@RestController
@RequestMapping("${sps.api.admin.endpoint.v1}" + API.USER_ACCOUNT_ENDPOINT)
public class AdminUserAccountController extends ActivatableEntityController<UserInfo, UserMod> {

    private static final Logger log = LoggerFactory.getLogger(AdminUserAccountController.class);

    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserDAO userDAO;
    private final PasswordEncoder userPasswordEncoder;
    private final EntityPrivilegeDAO entityPrivilegeDAO;
    private final EntityService entityService;

    public AdminUserAccountController(
            final UserDAO userDAO,
            final AuditLogDAO auditLogDAO,
            final EntityPrivilegeDAO entityPrivilegeDAO,
            final UserService userService,
            final PaginationService paginationService,
            final ApplicationEventPublisher applicationEventPublisher,
            final BeanValidationService beanValidationService,
            final EntityService entityService,
            @Qualifier(ServiceConfig.USER_PASSWORD_ENCODER_BEAN_NAME) final PasswordEncoder userPasswordEncoder) {

        super(userService, userDAO, auditLogDAO, paginationService, beanValidationService);
        this.entityPrivilegeDAO = entityPrivilegeDAO;
        this.applicationEventPublisher = applicationEventPublisher;
        this.userDAO = userDAO;
        this.entityService = entityService;
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
        this.auditLogDAO.logLogin(this.userService.getCurrentUser().getUserInfo());
    }

    @RequestMapping(path = API.LOGOUT_PATH_SEGMENT, method = RequestMethod.POST)
    public void logLogout() {
        this.auditLogDAO.logLogout(this.userService.getCurrentUser().getUserInfo());
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
                .flatMap(e -> this.auditLogDAO.log(this.userService.getCurrentUser().getUserInfo(), AuditLog.AuditLogType.PASSWORD_CHANGE, e))
                .getOrThrow();
    }

    @RequestMapping(
            path = API.USER_PRIVILEGES_ENDPOINT + API.PARAM_UUID_PATH_SEGMENT,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserPrivileges userPrivileges(@PathVariable(Domain.USER.ATTR_UUID) final String userUUID) {

        checkAdminRoleOrOwner(userUUID);

        return this.userService
                .getUserPrivileges(userUUID)
                .getOrThrow();
    }

    private void checkAdminRoleOrOwner(final String userUUID) {
        final String uuid = this.userService.getCurrentUser().uuid();
        if (userUUID.equals(uuid)) {
            return;
        }

        this.checkAdminRole();
    }

    @RequestMapping(
            path = API.USERSYNC_SEBSERVER_ENDPOINT,
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserInfo userSyncSEBServer(@RequestBody @Validated final UserMod userMod) {

        checkAdminRole();

        log.info("User synchronization request received for user: {}", userMod);

        return this.userService
                .synchronizeUserAccount(userMod)
                .getOrThrow();
    }

    @RequestMapping(
            path = API.ENTITY_PRIVILEGE_ENDPOINT,
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityPrivilege createEntityPrivilege(
            @RequestParam(name = Domain.ENTITY_PRIVILEGE.ATTR_ENTITY_TYPE, required = true) final String entityType,
            @RequestParam(name = Domain.ENTITY_PRIVILEGE.ATTR_ENTITY_ID, required = true) final String entityModelId,
            @RequestParam(name = Domain.ENTITY_PRIVILEGE.ATTR_USER_UUID, required = false) final String userUUID,
            @RequestParam(name = Domain.USER.ATTR_USERNAME, required = false) final String userName,
            @RequestParam(name = Domain.ENTITY_PRIVILEGE.ATTR_PRIVILEGES, required = true) final String privilege) {

        checkAdminRole();

        final EntityType type = EntityType.valueOf(entityType);
        final Long entityPK = this.entityService.getIdForModelId(entityModelId, type);

        if (entityPK == null) {
            throw new NoResourceFoundException(type, entityModelId);
        }

        String userId = userUUID;
        if (userId == null) {
            if (userName == null) {
                throw APIErrorException.ofMissingAttribute(
                        Domain.ENTITY_PRIVILEGE.ATTR_USER_UUID,
                        "createEntityPrivilege");
            }

            final Result<ServerUser> byUsername = this.userDAO.byUsername(userName);
            if (byUsername.hasError()) {
                throw new NoResourceFoundException(EntityType.USER, userName);
            }
            userId = byUsername.get().getUserInfo().uuid;
        }

        return this.entityPrivilegeDAO.addPrivilege(
                type,
                entityPK,
                userId,
                PrivilegeType.byFlag(privilege))
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
                existingEntity.id,
                existingEntity.uuid,
                modifyData.name,
                modifyData.surname,
                modifyData.username,
                modifyData.email,
                modifyData.language,
                modifyData.timeZone,
                modifyData.roles,
                null, null, null,
                existingEntity.entityPrivileges);
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
