/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import org.mybatis.dynamic.sql.SqlTable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.api.POSTMapper;
import ch.ethz.seb.sps.domain.model.user.AuditLog;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.AuditLogRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.dao.AuditLogDAO;
import ch.ethz.seb.sps.server.datalayer.dao.EntityDAO;
import ch.ethz.seb.sps.server.servicelayer.BeanValidationService;
import ch.ethz.seb.sps.server.servicelayer.PaginationService;
import ch.ethz.seb.sps.server.servicelayer.UserService;

@RestController
@RequestMapping("${sps.api.admin.endpoint.v1}" + API.LOG_ENDPOINT)
public class AdminAuditLogController extends EntityController<AuditLog, AuditLog> {

    public AdminAuditLogController(
            final UserService userService,
            final EntityDAO<AuditLog, AuditLog> entityDAO,
            final AuditLogDAO auditLogDAO,
            final PaginationService paginationService,
            final BeanValidationService beanValidationService) {

        super(userService, entityDAO, auditLogDAO, paginationService, beanValidationService);
    }

    @Override
    public AuditLog savePut(final String modelId, final AuditLog modifyData) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected AuditLog createNew(final POSTMapper postParams) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected AuditLog merge(final AuditLog modifyData, final AuditLog existingEntity) {
        return null;
    }

    @Override
    protected SqlTable getSQLTableOfEntity() {
        return AuditLogRecordDynamicSqlSupport.auditLogRecord;
    }
}
