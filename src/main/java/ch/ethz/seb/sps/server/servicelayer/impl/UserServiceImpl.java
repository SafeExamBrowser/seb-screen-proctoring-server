/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.seb.sps.domain.model.service.Exam;
import ch.ethz.seb.sps.server.datalayer.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.api.API.PrivilegeType;
import ch.ethz.seb.sps.domain.api.API.UserRole;
import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.ModelIdAware;
import ch.ethz.seb.sps.domain.model.OwnedEntity;
import ch.ethz.seb.sps.domain.model.WithEntityPrivileges;
import ch.ethz.seb.sps.domain.model.user.EntityPrivilege;
import ch.ethz.seb.sps.domain.model.user.ServerUser;
import ch.ethz.seb.sps.domain.model.user.UserInfo;
import ch.ethz.seb.sps.domain.model.user.UserMod;
import ch.ethz.seb.sps.domain.model.user.UserPrivileges;
import ch.ethz.seb.sps.server.servicelayer.EntityService;
import ch.ethz.seb.sps.server.servicelayer.UserService;
import ch.ethz.seb.sps.utils.Result;
import ch.ethz.seb.sps.utils.Utils;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final Collection<ExtractUserFromAuthenticationStrategy> extractStrategies;
    private final EnumMap<UserRole, Collection<Privilege>> rolePrivileges = new EnumMap<>(UserRole.class);
    private final UserDAO userDAO;
    private final EntityPrivilegeDAO entityPrivilegeDAO;
    private final EntityService entityService;

    public UserServiceImpl(
            final UserDAO userDAO,
            final EntityPrivilegeDAO entityPrivilegeDAO,
            final EntityService entityService,
            final AdditionalAttributesDAO additionalAttributesDAO,
            final Collection<ExtractUserFromAuthenticationStrategy> extractStrategies) {

        this.userDAO = userDAO;
        this.extractStrategies = extractStrategies;
        this.entityPrivilegeDAO = entityPrivilegeDAO;
        this.entityService = entityService;

        // admin privileges
        this.rolePrivileges.put(
                UserRole.ADMIN,
                Arrays.asList(
                        new Privilege(EntityType.USER, API.PRIVILEGES_WRITE),
                        new Privilege(EntityType.CLIENT_ACCESS, API.PRIVILEGES_WRITE),
                        new Privilege(EntityType.EXAM, API.PRIVILEGES_WRITE),
                        new Privilege(EntityType.SEB_GROUP, API.PRIVILEGES_WRITE),
                        new Privilege(EntityType.SCREENSHOT, API.PRIVILEGES_WRITE),
                        new Privilege(EntityType.SESSION, API.PRIVILEGES_WRITE),
                        new Privilege(EntityType.SCREENSHOT_DATA, API.PRIVILEGES_WRITE),
                        new Privilege(EntityType.AUDIT_LOG, API.PRIVILEGES_WRITE)));

        // proctor and teacher only has dedicated entity privileges
        this.rolePrivileges.put(UserRole.PROCTOR, Collections.emptyList());
        this.rolePrivileges.put(UserRole.TEACHER, Collections.emptyList());

        // TODO other roles...
    }

    @Override
    public ServerUser getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("No Authentication found within Springs SecurityContextHolder");
        }

        return extractFromPrincipal(authentication);
    }

    @Override
    public ServerUser extractFromPrincipal(final Principal principal) {
        for (final ExtractUserFromAuthenticationStrategy extractStrategy : this.extractStrategies) {
            try {
                final ServerUser user = extractStrategy.extract(principal);
                if (user != null) {
                    return user;
                }
            } catch (final Exception e) {
                log.error("Unexpected error while trying to extract user from principal: ", e);
            }
        }

        throw new IllegalArgumentException("Unable to extract internal user from Principal: " + principal);
    }

    @Override
    public boolean hasOwnerPrivilege(final UserInfo user, final Entity entity) {
        if (!(entity instanceof OwnedEntity)) {
            return false;
        }

        final String ownerId = ((OwnedEntity) entity).getOwnerId();
        if (ownerId == null) {
            return false;
        }
        return ownerId.contains(user.uuid);
    }

    @Override
    public boolean hasInstancePrivilege(final UserInfo user, final Entity entity, final PrivilegeType privilegeType) {
        if (!(entity instanceof WithEntityPrivileges)) {
            return false;
        }

        return WithEntityPrivileges.hasAccess(
                ((WithEntityPrivileges) entity).getEntityPrivileges(),
                user.uuid,
                privilegeType);
    }

    @Override
    public boolean hasGrant(final UserInfo user, final PrivilegeType privilegeType, final EntityType entityType) {

        return user.roles.stream()
                .map(UserRole::valueOf)
                .map(this.rolePrivileges::get)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .anyMatch(p -> p.entityType == entityType && p.privilegeTypes.contains(privilegeType));

    }

    @Override
    public boolean hasGrant(final UserInfo user, final PrivilegeType privilegeType, final Entity entity) {
        // first check overall entity type grant
        if (hasGrant(privilegeType, entity.entityType())) {
            return true;
        }

        // then check owner grant if owned entity
        if (this.hasOwnerPrivilege(user, entity)) {
            return true;
        }

        // then check entity based privileges
        if (entity instanceof WithEntityPrivileges) {
            switch (privilegeType) {
                case READ:
                    return WithEntityPrivileges.hasReadAccess((WithEntityPrivileges) entity, user.uuid);
                case MODIFY:
                    return WithEntityPrivileges.hasModifyAccess((WithEntityPrivileges) entity, user.uuid);
                case WRITE:
                    return WithEntityPrivileges.hasWriteAccess((WithEntityPrivileges) entity, user.uuid);
            }
        }

        return false;
    }

    @Override
    public Result<Set<Long>> getIdsWithReadEntityPrivilege(final EntityType entityType) {
        return Result.tryCatch(() -> {
            final String userUUID = this.getCurrentUser().uuid();

            // if owned entity type. get all owned entity id's if owned entity
            final Set<Long> ownedEntityIds = new HashSet<>();
            final EntityDAO<Entity, ModelIdAware> entityDAO = this.entityService.getEntityDAOForType(entityType);
            if (entityDAO instanceof OwnedEntityDAO) {
                ownedEntityIds.addAll(((OwnedEntityDAO) entityDAO)
                        .getAllOwnedEntityPKs(userUUID)
                        .getOr(Collections.emptySet()));
            }

            // all entity id's with entity grant
            final Set<Long> grantedEntityIds = this.entityPrivilegeDAO
                    .getEntityIdsWithPrivilegeForUser(entityType, userUUID, null)
                    .getOrThrow();

            return Utils.immutableSetOf(Stream
                    .of(ownedEntityIds, grantedEntityIds)
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet()));
        });
    }

    @Override
    public void applyWriteEntityPrivilegeGrant(
            final EntityType entityType,
            final Long entityId,
            final String userUUID) {

        try {

            this.entityPrivilegeDAO
                    .addPrivilege(entityType, entityId, userUUID, PrivilegeType.WRITE)
                    .getOrThrow();

        } catch (final Exception e) {
            log.error("Failed to apply write entity grant to entity tyoe: {} id: {} for user: {}",
                    entityType,
                    entityId,
                    userUUID);
        }
    }

    @Override
    public Result<UserInfo> synchronizeUserAccount(final UserMod userMod) {
        return this.userDAO.synchronizeUserAccount(userMod)
                .map(this::synchronizeUserPrivileges);
    }
    
    private UserInfo synchronizeUserPrivileges(final UserInfo userInfo) {
        try {

            UserPrivileges privileges = getUserPrivileges(userInfo.uuid)
                    .getOrThrow();
            
            boolean isAdmin = userInfo.roles.contains(UserRole.ADMIN.name());
            privileges.entityPrivileges.forEach( p -> {
                this.entityPrivilegeDAO.updatePrivileges(
                        p.id,
                        isAdmin ? PrivilegeType.WRITE : PrivilegeType.READ
                );
            });
            
        } catch (Exception e) {
            log.error("Failed to sync user privileges for user: {}", userInfo, e);
        }
        return userInfo;
    }

    @Override
    public Result<UserPrivileges> getUserPrivileges(final String userUUID) {
        return Result.tryCatch(() -> {

            final UserInfo userInfo = this.userDAO
                    .byModelId(userUUID)
                    .getOrThrow();

            final Collection<EntityPrivilege> entityPrivileges = this.entityPrivilegeDAO
                    .getEntityPrivilegesForUser(userUUID)
                    .getOrThrow();

            final Map<EntityType, PrivilegeType> typePrivileges = new EnumMap<>(EntityType.class);
            userInfo.roles.forEach(role -> {
                this.rolePrivileges.get(UserRole.valueOf(role))
                        .forEach(p -> typePrivileges.put(p.entityType, p.getHighest()));
            });

            return new UserPrivileges(userUUID, typePrivileges, entityPrivileges);
        });
    }

    @Override
    public Result<Exam> applyExamPrivileges(Exam exam) {
        return Result.tryCatch(() -> {

            if (exam.supporter == null || exam.supporter.isEmpty()) {
                return exam;
            }

            // remove all old privileges from the exam
            this.entityPrivilegeDAO.deleteAllPrivileges(EntityType.EXAM, exam.id)
                    .onError(error -> log.warn("Failed to delete old privileges for exam {}, error: {}",
                            exam,
                            error.getMessage()));

            // add new privileges according to the user role of user ids
            exam.supporter
                    .forEach(userUUID -> {
                        Result<UserInfo> userInfoResult = this.userDAO.byModelId(userUUID);
                        if (userInfoResult.hasError()) {
                            log.warn("Failed to get user to apply exam privilege. User: {}", userUUID);
                            return;
                        }

                        UserInfo userInfo = userInfoResult.get();
                        boolean readonlyActive = userInfo.roles.contains(UserRole.TEACHER.name()) &&
                                userInfo.roles.size() == 1;
                        this.entityPrivilegeDAO.addPrivilege(
                                EntityType.EXAM,
                                exam.id,
                                userUUID,
                                readonlyActive ? PrivilegeType.READ_ONLY_ACTIVE : PrivilegeType.READ)
                                .onError(error -> log.warn(
                                        "Failed to apply entity privilege for exam: {} and user: {}",
                                        exam,
                                        userUUID,
                                        error));
                    });
            return exam;
        });
    }

    public interface ExtractUserFromAuthenticationStrategy {
        ServerUser extract(Principal principal);
    }

    // 1. OAuth2Authentication strategy
    @Lazy
    @Component
    public static class DefaultUserExtractStrategy implements ExtractUserFromAuthenticationStrategy {
        
        final UserDAO userDAO;

        DefaultUserExtractStrategy(UserDAO userDAO) {
            this.userDAO = userDAO;
        }

        @Override
        public ServerUser extract(final Principal principal) {
            String name = principal.getName();
            return userDAO.byUsername(name)
                    .onError(error -> log.warn("Failed to find user for token authentication: {}", name))
                    .getOr(null);
        }
    }

    // 2. Separated thread strategy
    @Lazy
    @Component
    public static class OtherThreadUserExtractStrategy implements ExtractUserFromAuthenticationStrategy {

        @Override
        public ServerUser extract(final Principal principal) {
            if (principal instanceof ServerUser) {
                return (ServerUser) principal;
            }

            return null;
        }
    }

    private final static class Privilege {
        public final EntityType entityType;
        public final EnumSet<PrivilegeType> privilegeTypes;

        public Privilege(final EntityType entityType, final EnumSet<PrivilegeType> privilegeTypes) {
            this.entityType = entityType;
            this.privilegeTypes = privilegeTypes;
        }

        public PrivilegeType getHighest() {
            if (this.privilegeTypes == null) {
                return null;
            }

            if (this.privilegeTypes.contains(PrivilegeType.WRITE)) {
                return PrivilegeType.WRITE;
            } else if (this.privilegeTypes.contains(PrivilegeType.MODIFY)) {
                return PrivilegeType.MODIFY;
            } else if (this.privilegeTypes.contains(PrivilegeType.READ)) {
                return PrivilegeType.READ;
            }

            return null;
        }
    }

}
