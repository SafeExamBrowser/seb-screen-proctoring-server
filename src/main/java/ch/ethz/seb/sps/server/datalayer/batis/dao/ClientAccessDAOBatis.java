/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.batis.dao;

import java.util.List;

import org.mybatis.dynamic.sql.SqlBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ClientAccessRecordDynamicSqlSupport;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ClientAccessRecordMapper;
import ch.ethz.seb.sps.server.datalayer.batis.model.ClientAccessRecord;
import ch.ethz.seb.sps.server.servicelayer.dao.ClientAccessDAO;
import ch.ethz.seb.sps.server.servicelayer.dao.NoResourceFoundException;

@Service
@ConditionalOnExpression("'${sps.data.store.adapter}'.equals('FULL_RDBMS') or '${sps.data.store.adapter}'.equals('FILESYS_RDBMS')")
public class ClientAccessDAOBatis implements ClientAccessDAO {

    private final ClientAccessRecordMapper clientAccessRecordMapper;

    public ClientAccessDAOBatis(final ClientAccessRecordMapper clientAccessRecordMapper) {
        this.clientAccessRecordMapper = clientAccessRecordMapper;
    }

    @Override
    public CharSequence getEncodedClientPWD(final String clientId) {
        try {
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
        } catch (final Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
