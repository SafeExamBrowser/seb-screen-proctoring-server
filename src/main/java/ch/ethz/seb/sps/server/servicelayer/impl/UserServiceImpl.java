/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
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
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.api.API.PrivilegeType;
import ch.ethz.seb.sps.domain.api.API.UserRole;
import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.OwnedEntity;
import ch.ethz.seb.sps.domain.model.WithEntityPrivileges;
import ch.ethz.seb.sps.domain.model.user.ServerUser;
import ch.ethz.seb.sps.domain.model.user.UserInfo;
import ch.ethz.seb.sps.server.datalayer.dao.EntityPrivilegeDAO;
import ch.ethz.seb.sps.server.servicelayer.UserService;
import ch.ethz.seb.sps.utils.Result;
import ch.ethz.seb.sps.utils.Utils;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final Collection<ExtractUserFromAuthenticationStrategy> extractStrategies;
    private final EnumMap<UserRole, Collection<Privilege>> rolePrivileges = new EnumMap<>(UserRole.class);
    private final EntityPrivilegeDAO entityPrivilegeDAO;

    public UserServiceImpl(
            final EntityPrivilegeDAO entityPrivilegeDAO,
            final Collection<ExtractUserFromAuthenticationStrategy> extractStrategies) {

        this.extractStrategies = extractStrategies;
        this.entityPrivilegeDAO = entityPrivilegeDAO;

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
                        new Privilege(EntityType.SCREENSHOT_DATA, API.PRIVILEGES_WRITE)));

        // proctor only has dedicated entity privileges
        this.rolePrivileges.put(UserRole.PROCTOR, Collections.emptyList());

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
                log.error("Unexpected error while trying to extract user form principal: ", e);
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
                .map(role -> this.rolePrivileges.get(role))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(p -> p.entityType == entityType && p.privilegeTypes.contains(privilegeType))
                .findAny()
                .isPresent();

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
            return Utils.immutableSetOf(this.entityPrivilegeDAO
                    .getEntityIdsWithPrivilegeForUser(entityType, userUUID, null)
                    .getOrThrow());
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

    public interface ExtractUserFromAuthenticationStrategy {
        ServerUser extract(Principal principal);
    }

    // 1. OAuth2Authentication strategy
    @Lazy
    @Component
    public static class DefaultUserExtractStrategy implements ExtractUserFromAuthenticationStrategy {

        @Override
        public ServerUser extract(final Principal principal) {
            if (principal instanceof OAuth2Authentication) {
                final Authentication userAuthentication = ((OAuth2Authentication) principal).getUserAuthentication();
                //UsernamePasswordAuthenticationToken == initial request with username & password | PreAuthenticatedAuthenticationToken == request with refresh token
                if (userAuthentication instanceof UsernamePasswordAuthenticationToken
                        || userAuthentication instanceof PreAuthenticatedAuthenticationToken) {
                    final Object userPrincipal = userAuthentication.getPrincipal();
                    if (userPrincipal instanceof ServerUser) {
                        return (ServerUser) userPrincipal;
                    }
                }
            }

            return null;
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
    }

}
