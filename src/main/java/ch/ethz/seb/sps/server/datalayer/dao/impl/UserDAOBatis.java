/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao.impl;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.api.APIError.APIErrorType;
import ch.ethz.seb.sps.domain.api.APIErrorException;
import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.user.ServerUser;
import ch.ethz.seb.sps.domain.model.user.UserAccount;
import ch.ethz.seb.sps.domain.model.user.UserInfo;
import ch.ethz.seb.sps.domain.model.user.UserMod;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ClientAccessRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.UserRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.UserRecordMapper;
import ch.ethz.seb.sps.server.datalayer.batis.model.UserRecord;
import ch.ethz.seb.sps.server.datalayer.dao.DuplicateEntityException;
import ch.ethz.seb.sps.server.datalayer.dao.UserDAO;
import ch.ethz.seb.sps.utils.Constants;
import ch.ethz.seb.sps.utils.Result;
import ch.ethz.seb.sps.utils.Utils;

@Service
public class UserDAOBatis implements UserDAO {

    private final UserRecordMapper userRecordMapper;
    private final PasswordEncoder userPasswordEncoder;

    public UserDAOBatis(
            final UserRecordMapper userRecordMapper,
            final PasswordEncoder userPasswordEncoder) {

        super();
        this.userRecordMapper = userRecordMapper;
        this.userPasswordEncoder = userPasswordEncoder;
    }

    @Override
    public EntityType entityType() {
        return EntityType.USER;
    }

    @Override
    @Transactional(readOnly = true)
    public Result<ServerUser> byUsername(final String username) {
        return recordByUsername(username)
                .map(this::sebServerUserFromRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public Result<UserInfo> byPK(final Long id) {
        return Result.tryCatch(() -> this.userRecordMapper
                .selectByPrimaryKey(id))
                .map(this::toDomainModel);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isActive(final String modelId) {
        if (StringUtils.isBlank(modelId)) {
            return false;
        }

        return this.userRecordMapper.countByExample()
                .where(UserRecordDynamicSqlSupport.id, isEqualTo(Long.valueOf(modelId)))
                .and(UserRecordDynamicSqlSupport.terminationTime, SqlBuilder.isNull())
                .build()
                .execute() > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<UserInfo>> allMatching(final FilterMap filterMap, final Predicate<UserInfo> predicate) {
        return Result.tryCatch(() -> {
            final String userRoles = filterMap.getString(Domain.USER.ATTR_ROLES);
            final Predicate<UserInfo> _predicate = (StringUtils.isNotBlank(userRoles))
                    ? predicate.and(ui -> ui.roles.contains(userRoles))
                    : predicate;

            final Boolean active = filterMap.getBooleanObject(API.ACTIVE_FILTER);

            return this.userRecordMapper
                    .selectByExample()
                    .where(
                            UserRecordDynamicSqlSupport.terminationTime,
                            (active != null) ? active ? SqlBuilder.isNull() : SqlBuilder.isNotNull()
                                    : SqlBuilder.isEqualToWhenPresent(() -> null))

                    .and(
                            UserRecordDynamicSqlSupport.name,
                            isLikeWhenPresent(filterMap.getSQLWildcard(Domain.USER.ATTR_NAME)))
                    .and(
                            UserRecordDynamicSqlSupport.surname,
                            isLikeWhenPresent(filterMap.getSQLWildcard(Domain.USER.ATTR_SURNAME)))
                    .and(
                            UserRecordDynamicSqlSupport.username,
                            isLikeWhenPresent(filterMap.getSQLWildcard(Domain.USER.ATTR_USERNAME)))
                    .and(
                            UserRecordDynamicSqlSupport.email,
                            isLikeWhenPresent(filterMap.getSQLWildcard(Domain.USER.ATTR_EMAIL)))
                    .and(
                            UserRecordDynamicSqlSupport.language,
                            isLikeWhenPresent(filterMap.getString(Domain.USER.ATTR_LANGUAGE)))
                    .and(
                            ClientAccessRecordDynamicSqlSupport.creationTime,
                            SqlBuilder.isGreaterThanOrEqualToWhenPresent(
                                    filterMap.getLong(Domain.USER.ATTR_CREATION_TIME)))
                    .build()
                    .execute()
                    .stream()
                    .map(this::toDomainModel)
                    .filter(_predicate)
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<UserInfo>> allOf(final Set<Long> pks) {
        return Result.tryCatch(() -> {

            if (pks == null || pks.isEmpty()) {
                return Collections.emptyList();
            }

            return this.userRecordMapper.selectByExample()
                    .where(UserRecordDynamicSqlSupport.id, isIn(new ArrayList<>(pks)))
                    .build()
                    .execute()
                    .stream()
                    .map(this::toDomainModel)
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional
    public Result<UserInfo> createNew(final UserMod data) {
        return Result.tryCatch(() -> {

            if (!data.newPasswordMatch()) {
                throw new APIErrorException(APIErrorType.PASSWORD_MISMATCH);
            }

            checkUniqueUsername(data);
            checkUniqueMailAddress(data);

            final long now = Utils.getMillisecondsNow();

            final UserRecord recordToSave = new UserRecord(
                    null,
                    UUID.randomUUID().toString(),
                    data.name,
                    data.surname,
                    data.username,
                    this.userPasswordEncoder.encode(data.getNewPassword()),
                    data.email,
                    data.language.toLanguageTag(),
                    data.timeZone.getID(),
                    fromUserRoles(data.roles),
                    now,
                    now,
                    null);

            this.userRecordMapper.insert(recordToSave);
            final Long newUserPK = recordToSave.getId();
            final UserRecord newRecord = this.userRecordMapper
                    .selectByPrimaryKey(newUserPK);
            return newRecord;

        })
                .map(this::toDomainModel)
                .onError(TransactionHandler::rollback);
    }

    @Override
    @Transactional
    public Result<UserInfo> save(final UserInfo data) {
        return recordByUUID(data.uuid)
                .map(record -> {

                    checkUniqueUsername(data);
                    checkUniqueMailAddress(data);

                    final UserRecord newRecord = new UserRecord(
                            record.getId(),
                            null,
                            data.name,
                            data.surname,
                            data.username,
                            null,
                            (data.email == null) ? "" : data.email,
                            data.language.toLanguageTag(),
                            data.timeZone.getID(),
                            fromUserRoles(data.roles),
                            null,
                            Utils.getMillisecondsNow(),
                            null);

                    this.userRecordMapper.updateByPrimaryKeySelective(newRecord);
                    return this.userRecordMapper.selectByPrimaryKey(record.getId());
                })
                .map(this::toDomainModel)
                .onError(TransactionHandler::rollback);
    }

    @Override
    @Transactional
    public Result<Collection<EntityKey>> delete(final Set<EntityKey> all) {
        return Result.tryCatch(() -> {

            final List<Long> ids = extractListOfPKs(all);
            if (ids == null || ids.isEmpty()) {
                return Collections.emptyList();
            }

            // get all user records for later processing
            final List<UserRecord> users = this.userRecordMapper.selectByExample()
                    .where(UserRecordDynamicSqlSupport.id, isIn(ids))
                    .build()
                    .execute();

            // then delete the user account
            this.userRecordMapper.deleteByExample()
                    .where(UserRecordDynamicSqlSupport.id, isIn(ids))
                    .build()
                    .execute();

            return users.stream()
                    .map(rec -> new EntityKey(rec.getUuid(), EntityType.USER))
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional
    public Result<UserInfo> changePassword(final String modelId, final CharSequence newPassword) {
        return recordByUUID(modelId)
                .map(record -> {
                    final UserRecord newRecord = new UserRecord(
                            record.getId(),
                            null,
                            null,
                            null,
                            null,
                            this.userPasswordEncoder.encode(newPassword),
                            null,
                            null,
                            null,
                            null,
                            null,
                            Utils.getMillisecondsNow(),
                            null);
                    this.userRecordMapper.updateByPrimaryKeySelective(newRecord);
                    return this.userRecordMapper.selectByPrimaryKey(record.getId());
                })
                .map(this::toDomainModel)
                .onError(TransactionHandler::rollback);
    }

    @Override
    @Transactional
    public Result<Collection<EntityKey>> setActive(final Set<EntityKey> all, final boolean active) {
        return Result.tryCatch(() -> {

            final List<Long> ids = extractListOfPKs(all);
            if (ids == null || ids.isEmpty()) {
                return Collections.emptyList();
            }

            final long now = Utils.getMillisecondsNow();

            final UserRecord userRecord = new UserRecord(
                    null, null, null, null, null, null,
                    null, null, null, null, null,
                    now,
                    active ? null : now);

            this.userRecordMapper.updateByExampleSelective(userRecord)
                    .where(UserRecordDynamicSqlSupport.id, isIn(ids))
                    .build()
                    .execute();

            return this.userRecordMapper.selectByExample()
                    .where(UserRecordDynamicSqlSupport.id, isIn(ids))
                    .build()
                    .execute()
                    .stream()
                    .map(record -> new EntityKey(record.getUuid(), EntityType.USER))
                    .collect(Collectors.toList());
        });
    }

    private Result<UserRecord> recordByUsername(final String username) {
        return getSingleResource(
                username,
                this.userRecordMapper
                        .selectByExample()
                        .where(UserRecordDynamicSqlSupport.username, isEqualTo(username))
                        .and(UserRecordDynamicSqlSupport.terminationTime, SqlBuilder.isNull())
                        .build()
                        .execute());
    }

    private Result<UserRecord> recordByUUID(final String uuid) {
        return getSingleResource(
                uuid,
                this.userRecordMapper
                        .selectByExample()
                        .where(UserRecordDynamicSqlSupport.uuid, isEqualTo(uuid))
                        .build()
                        .execute());
    }

    private UserInfo toDomainModel(final UserRecord record) {

        final Set<String> roles = totUserRoles(record);

        return new UserInfo(
                record.getUuid(),
                record.getName(),
                record.getSurname(),
                record.getUsername(),
                record.getEmail(),
                Locale.forLanguageTag(record.getLanguage()),
                DateTimeZone.forID(record.getTimezone()),
                roles,
                record.getCreationTime(),
                record.getLastUpdateTime(),
                record.getTerminationTime());

    }

    private Set<String> totUserRoles(final UserRecord record) {
        return (record.getRoles() != null)
                ? new HashSet<>(Arrays.asList(StringUtils.split(record.getRoles(), Constants.LIST_SEPARATOR)))
                : null;
    }

    private String fromUserRoles(final Set<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }
        return StringUtils.join(roles, Constants.LIST_SEPARATOR);
    }

    private ServerUser sebServerUserFromRecord(final UserRecord record) {
        return new ServerUser(
                record.getId(),
                toDomainModel(record),
                record.getPassword());
    }

    private void checkUniqueUsername(final UserAccount userAccount) {
        // check same username already exists
        final Long otherUsersWithSameName = this.userRecordMapper
                .countByExample()
                .where(UserRecordDynamicSqlSupport.username, isEqualTo(userAccount.getUsername()))
                .and(UserRecordDynamicSqlSupport.uuid, isNotEqualToWhenPresent(userAccount.getModelId()))
                .build()
                .execute();

        if (otherUsersWithSameName != null && otherUsersWithSameName > 0) {
            throw new DuplicateEntityException(
                    EntityType.USER,
                    Domain.USER.ATTR_USERNAME,
                    "user:username:username.notunique");
        }
    }

    private void checkUniqueMailAddress(final UserAccount userAccount) {
        if (StringUtils.isBlank(userAccount.getEmail())) {
            return;
        }

        // check same email already exists
        final Long otherUsersWithSameName = this.userRecordMapper
                .countByExample()
                .where(UserRecordDynamicSqlSupport.email, isEqualTo(userAccount.getEmail()))
                .and(UserRecordDynamicSqlSupport.uuid, isNotEqualToWhenPresent(userAccount.getModelId()))
                .build()
                .execute();

        if (otherUsersWithSameName != null && otherUsersWithSameName > 0) {
            throw new DuplicateEntityException(
                    EntityType.USER,
                    Domain.USER.ATTR_EMAIL,
                    "user:email:email.notunique");
        }
    }

}
