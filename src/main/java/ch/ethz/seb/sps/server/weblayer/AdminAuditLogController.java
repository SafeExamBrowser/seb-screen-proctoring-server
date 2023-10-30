/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.api.APIErrorException;
import ch.ethz.seb.sps.domain.api.POSTMapper;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.service.Session;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import ch.ethz.seb.sps.domain.model.user.AuditLog;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.AuditLogRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.SessionRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.dao.AuditLogDAO;
import ch.ethz.seb.sps.server.datalayer.dao.EntityDAO;
import ch.ethz.seb.sps.server.datalayer.dao.GroupDAO;
import ch.ethz.seb.sps.server.datalayer.dao.SessionDAO;
import ch.ethz.seb.sps.server.servicelayer.BeanValidationService;
import ch.ethz.seb.sps.server.servicelayer.PaginationService;
import ch.ethz.seb.sps.server.servicelayer.UserService;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.SqlTable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static java.lang.Long.parseLong;

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
    protected AuditLog createNew(POSTMapper postParams) {

        final String auditLogModelId = postParams.getString(Domain.AUDIT_LOG.ATTR_ID);
        final Long entityId = (auditLogModelId != null) ? this.auditLogDAO.modelIdToPK(auditLogModelId) : null;

        return this.auditLogDAO.createNew(
                new AuditLog(
                        parseLong(auditLogModelId),
                        this.userService.getCurrentUserUUIDOrNull(),
                        this.userService.getCurrentUser().getUsername(),
                        null,
                        AuditLog.AuditLogType.CREATE,
                        EntityType.AUDIT_LOG,
                        entityId.toString(),
                        null
                )
        ).getOrThrow();
    }

    //implementing merge does not make sense --> logs should not mbe modified
    @Override
    protected AuditLog merge(AuditLog modifyData, AuditLog existingEntity) {
        return null;
    }

    @Override
    protected SqlTable getSQLTableOfEntity() {
        return AuditLogRecordDynamicSqlSupport.auditLogRecord;
    }
}
