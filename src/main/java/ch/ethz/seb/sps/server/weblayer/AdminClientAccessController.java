/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import java.util.UUID;

import org.mybatis.dynamic.sql.SqlTable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.api.POSTMapper;
import ch.ethz.seb.sps.domain.model.user.ClientAccess;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ClientAccessRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.dao.AuditLogDAO;
import ch.ethz.seb.sps.server.datalayer.dao.ClientAccessDAO;
import ch.ethz.seb.sps.server.servicelayer.BeanValidationService;
import ch.ethz.seb.sps.server.servicelayer.PaginationService;
import ch.ethz.seb.sps.server.servicelayer.UserService;

@RestController
@RequestMapping("${sps.api.admin.endpoint.v1}" + API.CLIENT_ACCESS_ENDPOINT)
public class AdminClientAccessController extends ActivatableEntityController<ClientAccess, ClientAccess> {

    public AdminClientAccessController(
            final UserService userService,
            final ClientAccessDAO entityDAO,
            final AuditLogDAO auditLogDAO,
            final PaginationService paginationService,
            final BeanValidationService beanValidationService) {

        super(userService, entityDAO, auditLogDAO, paginationService, beanValidationService);
    }

    @Override
    protected ClientAccess createNew(final POSTMapper postParams) {
        return new ClientAccess(
                null,
                UUID.randomUUID().toString(),
                postParams.getString(Domain.CLIENT_ACCESS.ATTR_NAME),
                postParams.getString(Domain.CLIENT_ACCESS.ATTR_DESCRIPTION),
                null, null, null, null, null, null, null);
    }

    @Override
    protected ClientAccess merge(final ClientAccess modifyData, final ClientAccess existingEntity) {
        return new ClientAccess(
                existingEntity.id,
                existingEntity.uuid,
                modifyData.name,
                modifyData.description,
                null, null, null, null, null, null, null);
    }

    @Override
    protected SqlTable getSQLTableOfEntity() {
        return ClientAccessRecordDynamicSqlSupport.clientAccessRecord;
    }

}
