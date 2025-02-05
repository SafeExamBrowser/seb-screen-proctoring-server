/*
 * Copyright (c) 2025 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.integrationtests.datalayer.batis.dao;

import static org.junit.Assert.*;

import ch.ethz.seb.sps.integrationtests.ServiceTest_FULL_RDBMS;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordMapper;
import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataRecord;
import ch.ethz.seb.sps.server.datalayer.dao.impl.S3DAO;
import ch.ethz.seb.sps.server.servicelayer.impl.ScreenshotStore_S3;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.mockito.Mock;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class ScreenshotS3FailTest extends ServiceTest_FULL_RDBMS {
    
//    @Autowired
//    private ScreenshotStore_S3 screenshotStore_S3;
    
//    @Mock
//    private S3DAO s3DAO;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    @Autowired
    private ScreenshotDataRecordMapper screenshotDataRecordMapper;

    @Test
    public void testNoDuplicateScreenshotDataStoresOnS3Fail() {

        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(this.sqlSessionFactory, ExecutorType.BATCH);

        ScreenshotDataRecord screenshotDataRecord = new ScreenshotDataRecord(null, "sessionId", 1738670400000L, 0, "metadata");

        assertNull(screenshotDataRecord.getId());

        screenshotDataRecordMapper.insert(screenshotDataRecord);
        sqlSessionTemplate.flushStatements();

        assertNotNull(screenshotDataRecord.getId());

        if (screenshotDataRecord.getId() == null) {
            screenshotDataRecordMapper.insert(screenshotDataRecord);
            sqlSessionTemplate.flushStatements();
        }
        
        Long num = screenshotDataRecordMapper.countByExample().build().execute();
        
        assertTrue(1 == num);

    }
}
