/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.user.AuditLog;
import ch.ethz.seb.sps.domain.model.user.AuditLog.AuditLogType;
import ch.ethz.seb.sps.domain.model.user.UserAccount;
import ch.ethz.seb.sps.domain.model.user.UserInfo;
import ch.ethz.seb.sps.server.datalayer.dao.AuditLogDAO;
import ch.ethz.seb.sps.utils.Result;

@Lazy
@Component
public class AuditLogDAOBatis implements AuditLogDAO {

    @Override
    public EntityType entityType() {
        return EntityType.AUDIT_LOG;
    }

    @Override
    public Result<AuditLog> byPK(final Long id) {
        // TODO Auto-generated method stub
        return Result.ofRuntimeError("TODO");
    }

    @Override
    public Result<Collection<AuditLog>> allOf(final Set<Long> pks) {
        // TODO Auto-generated method stub
        return Result.ofRuntimeError("TODO");
    }

    @Override
    public Result<AuditLog> createNew(final AuditLog data) {
        // TODO Auto-generated method stub
        return Result.ofRuntimeError("TODO");
    }

    @Override
    public Result<AuditLog> save(final AuditLog data) {
        // TODO Auto-generated method stub
        return Result.ofRuntimeError("TODO");
    }

    @Override
    public Result<Collection<EntityKey>> delete(final Set<EntityKey> all) {
        // TODO Auto-generated method stub
        return Result.ofRuntimeError("TODO");
    }

    @Override
    public Result<Collection<AuditLog>> allMatching(
            final FilterMap filterMap,
            final Collection<Long> prePredicated) {
        // TODO Auto-generated method stub
        return Result.ofRuntimeError("TODO");
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Set<Long>> getAllOwnedIds(final String userUUID) {
        return Result.of(Collections.emptySet());
    }

    @Override
    public void logLogout(final UserInfo userInfo) {
        // TODO Auto-generated method stub

    }

    @Override
    public <T extends Entity> Result<T> log(final AuditLogType logType, final T entity) {
        // TODO Auto-generated method stub
        return Result.of(entity);
    }

    @Override
    public <T extends Entity> Result<T> logCreate(final T entity) {
        // TODO Auto-generated method stub
        return Result.of(entity);
    }

    @Override
    public <T extends Entity> Result<T> logModify(final T entity) {
        // TODO Auto-generated method stub
        return Result.of(entity);
    }

    @Override
    public <T extends Entity> Result<T> logDelete(final T entity) {
        // TODO Auto-generated method stub
        return Result.of(entity);
    }

    @Override
    public Result<Collection<EntityKey>> logDeleted(final Collection<EntityKey> entities) {
        // TODO Auto-generated method stub
        return Result.of(entities);
    }

    @Override
    public void logLogin(final UserInfo userInfo) {
        // TODO Auto-generated method stub

    }

    @Override
    public Result<UserAccount> logRegisterAccount(final UserAccount account) {
        // TODO Auto-generated method stub
        return Result.of(account);
    }

}
