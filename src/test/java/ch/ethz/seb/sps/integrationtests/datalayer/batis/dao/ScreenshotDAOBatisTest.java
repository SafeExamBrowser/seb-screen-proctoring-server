/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.integrationtests.datalayer.batis.dao;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import ch.ethz.seb.sps.integrationtests.ServiceTest_FULL_RDBMS;
import ch.ethz.seb.sps.server.datalayer.dao.impl.ScreenshotDAOBatis;
import ch.ethz.seb.sps.utils.Result;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScreenshotDAOBatisTest extends ServiceTest_FULL_RDBMS {

    @Autowired
    private ScreenshotDAOBatis screenshotDAOBatis;

    @Test
    @Order(1)
    public void test01ServiceInit() throws Exception {
        assertNotNull(this.screenshotDAOBatis);
    }

    @Test
    @Order(2)
    public void test02StoreImage() throws Exception {
        final String sessionId = "test";
        final Result<Long> storeImage = this.screenshotDAOBatis
                .storeImage(1L, sessionId, new ByteArrayInputStream("TEST_STRING".getBytes()));

        if (storeImage.hasError()) {
            storeImage.getError().printStackTrace();
        }
        assertFalse(storeImage.hasError());
        assertEquals("1", String.valueOf(storeImage.get()));
    }

    @Test
    @Order(3)
    public void test03StoreAndGetImage() throws Exception {
        final String sessionId = "test";
        final Result<Long> storeImage = this.screenshotDAOBatis
                .storeImage(1L, sessionId, new ByteArrayInputStream("TEST_STRING".getBytes()));

        if (storeImage.hasError()) {
            storeImage.getError().printStackTrace();
        }
        assertFalse(storeImage.hasError());
        final Long newId = storeImage.get();
        assertEquals("1", String.valueOf(newId));

        final Result<InputStream> image = this.screenshotDAOBatis.getImage(newId, sessionId);

        if (image.hasError()) {
            image.getError().printStackTrace();
        }
        assertFalse(image.hasError());
        assertEquals("TEST_STRING", new String(image.get().readAllBytes()));
    }

    @Test
    @Order(4)
    public void test04StoreTwoImages() throws Exception {
        final String sessionId = "test";
        final Result<Long> storeImage1 = this.screenshotDAOBatis
                .storeImage(1L, sessionId, new ByteArrayInputStream("TEST_STRING".getBytes()));

        if (storeImage1.hasError()) {
            storeImage1.getError().printStackTrace();
        }
        assertFalse(storeImage1.hasError());
        final Long newId = storeImage1.get();
        assertEquals("1", String.valueOf(newId));

        final Result<Long> storeImage2 = this.screenshotDAOBatis
                .storeImage(2L, sessionId, new ByteArrayInputStream("TEST_STRING123".getBytes()));

        if (storeImage2.hasError()) {
            storeImage2.getError().printStackTrace();
        }
        assertFalse(storeImage2.hasError());
        final Long newId2 = storeImage2.get();
        assertEquals("2", String.valueOf(newId2));

        final Result<InputStream> image = this.screenshotDAOBatis.getImage(newId2, sessionId);

        if (image.hasError()) {
            image.getError().printStackTrace();
        }
        assertFalse(image.hasError());
        assertEquals("TEST_STRING123", new String(image.get().readAllBytes()));
    }

    @Test
    @Order(5)
    public void test05FailToStoreImageTwice_SamePK() throws Exception {
        final Result<Long> storeImage1 = this.screenshotDAOBatis
                .storeImage(1L, "test", new ByteArrayInputStream("TEST_STRING".getBytes()));

        if (storeImage1.hasError()) {
            storeImage1.getError().printStackTrace();
        }
        assertFalse(storeImage1.hasError());
        final Long newId = storeImage1.get();
        assertEquals("1", String.valueOf(newId));

        final Result<Long> storeImage2 = this.screenshotDAOBatis
                .storeImage(1L, "test", new ByteArrayInputStream("TEST_STRING123".getBytes()));

        assertTrue(storeImage2.hasError());
        assertTrue(storeImage2.getError().getMessage().contains("JdbcSQLIntegrityConstraintViolationException"));
    }

}
