/*
 * Copyright (c) 2025 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.integrationtests.datalayer.batis.dao;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;

import ch.ethz.seb.sps.domain.model.service.Session;
import ch.ethz.seb.sps.integrationtests.ServiceTest_FULL_RDBMS;
import ch.ethz.seb.sps.server.ServiceConfig;
import ch.ethz.seb.sps.server.datalayer.batis.mapper.ScreenshotDataRecordMapper;
import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataRecord;
import ch.ethz.seb.sps.server.datalayer.dao.impl.S3DAO;
import ch.ethz.seb.sps.server.servicelayer.impl.ScreenshotStore_S3;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.transaction.PlatformTransactionManager;


public class ScreenshotS3FailTest extends ServiceTest_FULL_RDBMS {
    

    @Mock
    private S3DAO s3DAO;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    @Autowired
    private ScreenshotDataRecordMapper screenshotDataRecordMapper;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    @Qualifier(value = ServiceConfig.SCREENSHOT_STORE_API_EXECUTOR) 
    private TaskScheduler taskScheduler;

    @Test
    public void testNoDuplicateScreenshotDataStores() {

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

        assertEquals(1, (long) num);

    }

    @Test
    public void testNoDuplicateScreenshotDataStoresOnS3Fail() {
        
        Mockito.when(s3DAO.uploadItem(Mockito.any(), Mockito.any(),Mockito.any()))
                .thenThrow(new IllegalStateException("Test: No space left"));

        ScreenshotStore_S3 screenshotStore_S3 = new ScreenshotStore_S3(
                sqlSessionFactory,
                transactionManager,
                s3DAO,
                taskScheduler,
                1000, 
                false
        );

        screenshotStore_S3.init();

        screenshotStore_S3.storeScreenshot("session123", 100L, Session.ImageFormat.PNG, "metadata", new ByteArrayInputStream(new byte[] {1, 2}));
        
        // wait some time 
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            
        }

        Long num = screenshotDataRecordMapper.countByExample().build().execute();
        assertEquals(1, (long) num);
    }
}
