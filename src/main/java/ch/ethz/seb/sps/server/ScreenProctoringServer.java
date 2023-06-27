/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

import ch.ethz.seb.sps.server.datalayer.batis.BatisConfig;

@SpringBootApplication(exclude = {
        UserDetailsServiceAutoConfiguration.class,
})
@EnableCaching
@Import(BatisConfig.class)
public class ScreenProctoringServer {

    public static void main(final String[] args) {
        try {
            org.apache.ibatis.logging.LogFactory.useSlf4jLogging();
            SpringApplication.run(ScreenProctoringServer.class, args);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

}
