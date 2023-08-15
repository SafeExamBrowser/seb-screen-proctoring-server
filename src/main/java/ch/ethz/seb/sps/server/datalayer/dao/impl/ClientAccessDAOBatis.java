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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.EntityPrivilege;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.user.ClientAccess;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ClientAccessRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ClientAccessRecordMapper;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.UserRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.model.ClientAccessRecord;
import ch.ethz.seb.sps.server.datalayer.dao.ClientAccessDAO;
import ch.ethz.seb.sps.server.datalayer.dao.DuplicateEntityException;
import ch.ethz.seb.sps.server.datalayer.dao.EntityPrivilegeDAO;
import ch.ethz.seb.sps.server.datalayer.dao.NoResourceFoundException;
import ch.ethz.seb.sps.server.servicelayer.ClientCredentialService;
import ch.ethz.seb.sps.server.servicelayer.UserService;
import ch.ethz.seb.sps.utils.Result;
import ch.ethz.seb.sps.utils.Utils;

@Service
public class ClientAccessDAOBatis implements ClientAccessDAO {

    private final ClientAccessRecordMapper clientAccessRecordMapper;
    private final ClientCredentialService clientCredentialService;
    private final EntityPrivilegeDAO entityPrivilegeDAO;
    private final UserService userService;

    public ClientAccessDAOBatis(
            final ClientAccessRecordMapper clientAccessRecordMapper,
            final ClientCredentialService clientCredentialService,
            final EntityPrivilegeDAO entityPrivilegeDAO,
            final UserService userService) {

        this.clientAccessRecordMapper = clientAccessRecordMapper;
        this.clientCredentialService = clientCredentialService;
        this.entityPrivilegeDAO = entityPrivilegeDAO;
        this.userService = userService;
    }

    @Override
    public EntityType entityType() {
        return EntityType.CLIENT_ACCESS;
    }

    @Override
    @Transactional(readOnly = true)
    public Result<ClientAccess> byPK(final Long id) {
        return Result.tryCatch(() -> this.clientAccessRecordMapper
                .selectByPrimaryKey(id))
                .map(this::toDomainModel);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isActive(final String modelId) {
        if (StringUtils.isBlank(modelId)) {
            return false;
        }

        return this.clientAccessRecordMapper
                .countByExample()
                .where(ClientAccessRecordDynamicSqlSupport.id, isEqualTo(Long.valueOf(modelId)))
                .and(ClientAccessRecordDynamicSqlSupport.terminationTime, SqlBuilder.isNull())
                .build()
                .execute() > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<ClientAccess>> allOf(final Set<Long> pks) {
        return Result.tryCatch(() -> {

            if (pks == null || pks.isEmpty()) {
                return Collections.emptyList();
            }

            return this.clientAccessRecordMapper
                    .selectByExample()
                    .where(ClientAccessRecordDynamicSqlSupport.id, isIn(new ArrayList<>(pks)))
                    .build()
                    .execute()
                    .stream()
                    .map(this::toDomainModel)
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Collection<ClientAccess>> allMatching(
            final FilterMap filterMap,
            final Predicate<ClientAccess> predicate) {

        return Result.tryCatch(() -> {

            final Boolean active = filterMap.getBooleanObject(API.ACTIVE_FILTER);

            return this.clientAccessRecordMapper
                    .selectByExample()
                    .where(
                            UserRecordDynamicSqlSupport.terminationTime,
                            (active != null) ? active ? SqlBuilder.isNull() : SqlBuilder.isNotNull()
                                    : SqlBuilder.isEqualToWhenPresent(() -> null))
                    .and(
                            ClientAccessRecordDynamicSqlSupport.name,
                            isLikeWhenPresent(filterMap.getSQLWildcard(Domain.CLIENT_ACCESS.ATTR_NAME)))
                    .and(
                            ClientAccessRecordDynamicSqlSupport.description,
                            isLikeWhenPresent(filterMap.getSQLWildcard(Domain.CLIENT_ACCESS.ATTR_DESCRIPTION)))
                    .and(
                            ClientAccessRecordDynamicSqlSupport.clientName,
                            isLikeWhenPresent(filterMap.getSQLWildcard(Domain.CLIENT_ACCESS.ATTR_CLIENT_NAME)))
                    .and(
                            ClientAccessRecordDynamicSqlSupport.creationTime,
                            SqlBuilder.isGreaterThanOrEqualToWhenPresent(
                                    filterMap.getLong(Domain.CLIENT_ACCESS.ATTR_CREATION_TIME)))

                    .build()
                    .execute()
                    .stream()
                    .map(this::toDomainModel)
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional
    public Result<ClientAccess> createNew(final ClientAccess data) {
        return this.clientCredentialService
                .generatedClientCredentials()
                .map(cc -> {

                    checkUniqueName(data);

                    final long millisecondsNow = Utils.getMillisecondsNow();

                    final ClientAccessRecord newRecord = new ClientAccessRecord(
                            null,
                            (StringUtils.isNotBlank(data.uuid))
                                    ? data.uuid
                                    : UUID.randomUUID().toString(),
                            data.name,
                            data.description,
                            cc.clientIdAsString(),
                            cc.secretAsString(),
                            this.userService.getCurrentUserUUIDOrNull(),
                            millisecondsNow,
                            millisecondsNow,
                            null);

                    this.clientAccessRecordMapper.insert(newRecord);
                    return newRecord;
                })
                .map(this::toDomainModel)
                .onError(TransactionHandler::rollback);
    }

    @Override
    @Transactional
    public Result<ClientAccess> save(final ClientAccess data) {
        return Result.tryCatch(() -> {

            checkUniqueName(data);

            final long millisecondsNow = Utils.getMillisecondsNow();
            final ClientAccessRecord newRecord = new ClientAccessRecord(
                    data.id,
                    null,
                    data.name,
                    data.description,
                    null,
                    null,
                    null,
                    null,
                    millisecondsNow,
                    null);

            this.clientAccessRecordMapper.updateByPrimaryKeySelective(newRecord);
            return this.clientAccessRecordMapper.selectByPrimaryKey(data.id);
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

            final ClientAccessRecord newRecord = new ClientAccessRecord(
                    null, null, null, null, null, null, null, null,
                    now,
                    active ? null : now);

            this.clientAccessRecordMapper.updateByExampleSelective(newRecord)
                    .where(ClientAccessRecordDynamicSqlSupport.id, isIn(ids))
                    .build()
                    .execute();

            return this.clientAccessRecordMapper.selectByExample()
                    .where(ClientAccessRecordDynamicSqlSupport.id, isIn(ids))
                    .build()
                    .execute()
                    .stream()
                    .map(record -> new EntityKey(record.getId(), EntityType.CLIENT_ACCESS))
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<CharSequence> getEncodedClientPWD(final String clientId) {
        return Result.tryCatch(() -> {

            final List<ClientAccessRecord> execute = this.clientAccessRecordMapper
                    .selectByExample()
                    .where(ClientAccessRecordDynamicSqlSupport.clientName, SqlBuilder.isEqualTo(clientId))
                    .build()
                    .execute();

            if (execute == null) {
                throw new NoResourceFoundException(EntityType.CLIENT_ACCESS, clientId);
            }
            if (execute.size() != 1) {
                throw new IllegalStateException("Expected one client but found more for: " + clientId);
            }

            return execute.get(0).getClientSecret();
        });

    }

    @Override
    @Transactional
    public Result<Collection<EntityKey>> delete(final Set<EntityKey> all) {
        return Result.tryCatch(() -> {

            final List<Long> ids = extractListOfPKs(all);
            if (ids == null || ids.isEmpty()) {
                return Collections.emptyList();
            }

            // get all client access records for later processing
            final List<ClientAccessRecord> clientAccess = this.clientAccessRecordMapper
                    .selectByExample()
                    .where(ClientAccessRecordDynamicSqlSupport.id, isIn(ids))
                    .build()
                    .execute();

            // then delete the client access
            this.clientAccessRecordMapper
                    .deleteByExample()
                    .where(ClientAccessRecordDynamicSqlSupport.id, isIn(ids))
                    .build()
                    .execute();

            return clientAccess.stream()
                    .map(rec -> new EntityKey(rec.getId(), EntityType.CLIENT_ACCESS))
                    .collect(Collectors.toList());
        });
    }

    private ClientAccess toDomainModel(final ClientAccessRecord record) {
        return new ClientAccess(
                record.getId(),
                record.getUuid(),
                record.getName(),
                record.getDescription(),
                record.getClientName(),
                record.getClientSecret(),
                record.getOwner(),
                getEntityPrivileges(record.getId()),
                record.getCreationTime(),
                record.getLastUpdateTime(),
                record.getTerminationTime());
    }

    private Collection<EntityPrivilege> getEntityPrivileges(final Long id) {
        try {

            if (id == null) {
                return Collections.emptyList();
            }

            return this.entityPrivilegeDAO
                    .getEntityPrivileges(EntityType.CLIENT_ACCESS, id)
                    .getOrThrow();

        } catch (final Exception e) {
            log.error("Failed to get entity privileges for ClientAccess: {}", id, e);
            return Collections.emptyList();
        }
    }

    // check if same name already exists for the same institution
    // if true an APIMessageException with a field validation error is thrown
    private void checkUniqueName(final ClientAccess sebClientConfig) {

        final Long otherWithSameName = this.clientAccessRecordMapper
                .countByExample()
                .where(ClientAccessRecordDynamicSqlSupport.name, isEqualTo(sebClientConfig.name))
                .and(ClientAccessRecordDynamicSqlSupport.id, isNotEqualToWhenPresent(sebClientConfig.id))
                .build()
                .execute();

        if (otherWithSameName != null && otherWithSameName > 0) {
            throw new DuplicateEntityException(
                    EntityType.CLIENT_ACCESS,
                    Domain.CLIENT_ACCESS.ATTR_NAME,
                    "clientaccess:name:name.notunique");
        }
    }

}
