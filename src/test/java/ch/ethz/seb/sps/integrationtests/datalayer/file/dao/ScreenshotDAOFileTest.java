/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.integrationtests.datalayer.file.dao;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import ch.ethz.seb.sps.integrationtests.datalayer.ServiceTest_FILESYS_RDBMS;
import ch.ethz.seb.sps.server.datalayer.dao.impl.ScreenshotDAOFile;
import ch.ethz.seb.sps.utils.Result;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScreenshotDAOFileTest extends ServiceTest_FILESYS_RDBMS {

    @Autowired
    private ScreenshotDAOFile screenshotDAOFile;

    @Test
    @Order(1)
    public void test01ServiceInit() throws Exception {
        assertNotNull(this.screenshotDAOFile);
    }

    @Test
    @Order(2)
    public void test02StoreImage() throws Exception {
        final String sessionId = "session1";
        final Result<Long> storeImage = this.screenshotDAOFile
                .storeImage(
                        1L,
                        sessionId,
                        new ByteArrayInputStream("TEST_STRING".getBytes()));

        if (storeImage.hasError()) {
            storeImage.getError().printStackTrace();
        }
        assertFalse(storeImage.hasError());
        assertEquals("1", String.valueOf(storeImage.get()));
    }

    @Test
    @Order(2)
    public void test03GetStoredImage() throws Exception {
        final String sessionId = "session1";
        final Result<InputStream> getImage = this.screenshotDAOFile
                .getImage(1L, sessionId);

        if (getImage.hasError()) {
            getImage.getError().printStackTrace();
        }
        assertFalse(getImage.hasError());
        assertEquals("TEST_STRING", new String(getImage.get().readAllBytes()));
    }

}
