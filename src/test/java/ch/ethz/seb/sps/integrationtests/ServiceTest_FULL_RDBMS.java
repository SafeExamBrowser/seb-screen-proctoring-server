/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.integrationtests;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import ch.ethz.seb.sps.server.ScreenProctoringServer;

@RunWith(SpringRunner.class)
@SpringBootTest(
        properties = "file.encoding=UTF-8",
        classes = { ScreenProctoringServer.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = { "classpath:schema-test.sql", "classpath:test-data.sql" })
public abstract class ServiceTest_FULL_RDBMS {

}
