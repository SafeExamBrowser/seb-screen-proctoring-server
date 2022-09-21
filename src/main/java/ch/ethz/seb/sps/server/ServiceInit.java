/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Import(DataSourceAutoConfiguration.class)
public class ServiceInit implements ApplicationListener<ApplicationReadyEvent> {

    public static final Logger INIT_LOGGER = LoggerFactory.getLogger("ch.ethz.seb.SERVICE_INIT");

    private final ApplicationContext applicationContext;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final Environment environment;
    private final ServiceInfo serviceInfo;
    private final SEBServerMigrationStrategy sebServerMigrationStrategy;
    private final AdminUserInitializer adminUserInitializer;
    private final boolean initialized = false;

    public ServiceInit(
            final ApplicationContext applicationContext,
            final ApplicationEventPublisher applicationEventPublisher,
            final ServiceInfo serviceInfo,
            final SEBServerMigrationStrategy sebServerMigrationStrategy,
            final AdminUserInitializer adminUserInitializer) {

        this.applicationContext = applicationContext;
        this.applicationEventPublisher = applicationEventPublisher;
        this.environment = applicationContext.getEnvironment();
        this.serviceInfo = serviceInfo;
        this.sebServerMigrationStrategy = sebServerMigrationStrategy;
        this.adminUserInitializer = adminUserInitializer;
    }

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {

        if (this.initialized) {
            return;
        }

        INIT_LOGGER.info(
                "---->       _______. _______ .______           _______. _______ .______     ____    ____  _______ .______                                                                         \r\n");
        INIT_LOGGER.info(
                "---->      /       ||   ____||   _  \\         /       ||   ____||   _  \\    \\   \\  /   / |   ____||   _  \\                                                                        \r\n");
        INIT_LOGGER.info(
                "---->     |   (----`|  |__   |  |_)  |       |   (----`|  |__   |  |_)  |    \\   \\/   /  |  |__   |  |_)  |                                                                       \r\n");
        INIT_LOGGER.info(
                "---->      \\   \\    |   __|  |   _  <         \\   \\    |   __|  |      /      \\      /   |   __|  |      /                                                                        \r\n");
        INIT_LOGGER.info(
                "---->  .----)   |   |  |____ |  |_)  |    .----)   |   |  |____ |  |\\  \\----.  \\    /    |  |____ |  |\\  \\----.                                                                   \r\n");
        INIT_LOGGER.info(
                "---->  |___________.|_______|.______/     |_______/ ________.__|| __.`.__.______\\__._____________|______.__________ .___________.  ______   .______       __  .__   __.   _______ \r\n");
        INIT_LOGGER.info(
                "---->      /       | /      ||   _  \\     |   ____||   ____||  \\ |  |    |   _  \\  |   _  \\      /  __  \\   /      ||           | /  __  \\  |   _  \\     |  | |  \\ |  |  /  _____|\r\n");
        INIT_LOGGER.info(
                "---->     |   (----`|  ,----'|  |_)  |    |  |__   |  |__   |   \\|  |    |  |_)  | |  |_)  |    |  |  |  | |  ,----'`---|  |----`|  |  |  | |  |_)  |    |  | |   \\|  | |  |  __  \r\n");
        INIT_LOGGER.info(
                "---->      \\   \\    |  |     |      /     |   __|  |   __|  |  . `  |    |   ___/  |      /     |  |  |  | |  |         |  |     |  |  |  | |      /     |  | |  . `  | |  | |_ | \r\n");
        INIT_LOGGER.info(
                "---->  .----)   |   |  `----.|  |\\  \\----.|  |____ |  |____ |  |\\   |    |  |      |  |\\  \\----.|  `--'  | |  `----.    |  |     |  `--'  | |  |\\  \\----.|  | |  |\\   | |  |__| | \r\n");
        INIT_LOGGER.info(
                "---->  |_______/     \\______|| _| `._____||_______||_______||__| \\__|    | _|      | _| `._____| \\______/   \\______|    |__|      \\______/  | _| `._____||__| |__| \\__|  \\______| \r\n");
        INIT_LOGGER.info(
                "---->                                                                                                                                                                             ");
        INIT_LOGGER.info("---->");
        INIT_LOGGER.info("---->");
        INIT_LOGGER.info("----> Version: {}", this.serviceInfo.getVersion());
        INIT_LOGGER.info("---->");
        INIT_LOGGER.info("----> Active profiles: {}", Arrays.toString(this.environment.getActiveProfiles()));
        INIT_LOGGER.info("---->");
        INIT_LOGGER.info("----> Context Path: {}", this.environment.getProperty("server.servlet.context-path"));
        INIT_LOGGER.info("---->");

        // do migration
        this.sebServerMigrationStrategy.applyMigration();

        INIT_LOGGER.info("----> ");
        INIT_LOGGER.info("----> Initialize Services...");
        INIT_LOGGER.info("----> ");

        this.applicationEventPublisher.publishEvent(new ServiceInitEvent(this));

        // Create an initial admin account if requested and not already in the data-base
        this.adminUserInitializer.initAdminAccount();

        INIT_LOGGER.info("----> *********************************************************");
        INIT_LOGGER.info("----> *** Webservice Info:                                  ***");
        INIT_LOGGER.info("----> *********************************************************");
        INIT_LOGGER.info("---->");

        INIT_LOGGER.info("----> JDBC connection pool max size: {}",
                this.environment.getProperty("spring.datasource.hikari.maximumPoolSize"));

    }

}
