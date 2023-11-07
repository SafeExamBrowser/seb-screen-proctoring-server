/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server;

import java.util.Arrays;

import javax.annotation.PreDestroy;

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

import ch.ethz.seb.sps.server.datalayer.batis.BatisConfig;

@Component
@Import({ DataSourceAutoConfiguration.class, BatisConfig.class })
public class ServiceInit implements ApplicationListener<ApplicationReadyEvent> {

    public static final Logger INIT_LOGGER = LoggerFactory.getLogger("SERVICE_INIT");

    private final ApplicationContext applicationContext;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final Environment environment;
    private final ServiceInfo serviceInfo;
    private final MigrationStrategy sebServerMigrationStrategy;
    private final AdminUserInitializer adminUserInitializer;

    private final boolean initialized = false;

    public ServiceInit(
            final ApplicationContext applicationContext,
            final ApplicationEventPublisher applicationEventPublisher,
            final ServiceInfo serviceInfo,
            final MigrationStrategy sebServerMigrationStrategy,
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

        // @formatter:off
        INIT_LOGGER.info(" _______ _______ _______      _______ _______ ______   __   __ ___ _______ _______                                                   ");
        INIT_LOGGER.info("|       |       |  _    |    |       |       |    _ | |  | |  |   |       |       |                                                  ");
        INIT_LOGGER.info("|  _____|    ___| |_|   |    |  _____|    ___|   | || |  |_|  |   |       |    ___|                                                  ");
        INIT_LOGGER.info("| |_____|   |___|       |    | |_____|   |___|   |_||_|       |   |       |   |___                                                   ");
        INIT_LOGGER.info("|_____  |    ___|  _   |     |_____  |    ___|    __  |       |   |      _|    ___|                                                  ");
        INIT_LOGGER.info(" _____| |   |___| |_|   |     _____| |   |___|   |  | ||     ||   |     |_|   |___                                                   ");
        INIT_LOGGER.info("|_______|_______|_______| ___|_______|_______|___|  |_|_|___|_|___|_______|_______|____ _______ _______ ______   ___ __    _ _______ ");
        INIT_LOGGER.info("|       |       |    _ | |       |       |  |  | |    |       |    _ | |       |       |       |       |    _ | |   |  |  | |       |");
        INIT_LOGGER.info("|  _____|       |   | || |    ___|    ___|   |_| |    |    _  |   | || |   _   |       |_     _|   _   |   | || |   |   |_| |    ___|");
        INIT_LOGGER.info("| |_____|       |   |_||_|   |___|   |___|       |    |   |_| |   |_||_|  | |  |       | |   | |  | |  |   |_||_|   |       |   | __ ");
        INIT_LOGGER.info("|_____  |      _|    __  |    ___|    ___|  _    |    |    ___|    __  |  |_|  |      _| |   | |  |_|  |    __  |   |  _    |   ||  |");
        INIT_LOGGER.info(" _____| |     |_|   |  | |   |___|   |___| | |   |    |   |   |   |  | |       |     |_  |   | |       |   |  | |   | | |   |   |_| |");
        INIT_LOGGER.info("|_______|_______|___|  |_|_______|_______|_|  |__|    |___|   |___|  |_|_______|_______| |___| |_______|___|  |_|___|_|  |__|_______|");
        // @formatter:on
        INIT_LOGGER.info("---->");
        INIT_LOGGER.info("---->");
        INIT_LOGGER.info("----> Version: {}", this.serviceInfo.getVersion());
        INIT_LOGGER.info("---->");
        INIT_LOGGER.info("----> Active profiles: {}", Arrays.toString(this.environment.getActiveProfiles()));
        INIT_LOGGER.info("---->");
        INIT_LOGGER.info("----> Context Path: {}", this.environment.getProperty("server.servlet.context-path"));
        INIT_LOGGER.info("---->");

        INIT_LOGGER.info("----> *********************************************************");
        INIT_LOGGER.info("----> *** Webservice starting up...                         ***");
        INIT_LOGGER.info("----> *********************************************************");
        INIT_LOGGER.info("----> ");
        INIT_LOGGER.info("----> Register Webservice: {}", this.serviceInfo.getWebserviceUUID());

        if (this.serviceInfo.isWebserviceInitialized()) {
            this.registerWebservice();

            // Apply migration if needed and possible
            INIT_LOGGER.info("----> ");
            this.sebServerMigrationStrategy.applyMigration();
            INIT_LOGGER.info("----> ");

        } else {

            // Apply migration if needed and possible
            INIT_LOGGER.info("----> ");
            this.sebServerMigrationStrategy.applyMigration();
            INIT_LOGGER.info("----> ");

            this.registerWebservice();
        }

        INIT_LOGGER.info("----> ");
        INIT_LOGGER.info("----> **** Initialize Services... *****************************");
        INIT_LOGGER.info("----> ");

        this.applicationEventPublisher.publishEvent(new ServiceInitEvent(this));

        INIT_LOGGER.info("----> ");
        INIT_LOGGER.info("----> **** Services initialized *******************************");
        INIT_LOGGER.info("----> ");

        // Create an initial admin account if requested and not already in the data-base
        this.adminUserInitializer.initAccounts();

        INIT_LOGGER.info("----> *********************************************************");
        INIT_LOGGER.info("----> *** Webservice Info:                                  ***");
        INIT_LOGGER.info("----> *********************************************************");
        INIT_LOGGER.info("---->");
        INIT_LOGGER.info("----> JDBC connection pool max size: {}",
                this.environment.getProperty("spring.datasource.hikari.maximumPoolSize"));
        INIT_LOGGER.info("----> Admin access token validity seconds: {}", this.serviceInfo.getAdminAccessTokenValSec());
        INIT_LOGGER.info("----> Admin refresh token validity seconds: {}",
                this.serviceInfo.getAdminRefreshTokenValSec());
        INIT_LOGGER.info("----> SEB session access token validity seconds: {}",
                this.serviceInfo.getSessionAPITokenValiditySeconds());
        INIT_LOGGER.info("----> ");
        INIT_LOGGER.info("----> Webservice context path: {}", this.serviceInfo.getContextPath());
        INIT_LOGGER.info("----> ");
        INIT_LOGGER.info("----> Internal Server address: {}", this.environment.getProperty("server.address"));
        INIT_LOGGER.info("----> Internal Server port: {}", this.environment.getProperty("server.port"));
        INIT_LOGGER.info("---->");
        INIT_LOGGER.info("----> Local-Host address: {}", this.serviceInfo.getLocalHostAddress());
        INIT_LOGGER.info("----> Local-Host name: {}", this.serviceInfo.getLocalHostName());
        INIT_LOGGER.info("---->");
        INIT_LOGGER.info("----> Remote-Host address: {}", this.serviceInfo.getLoopbackHostAddress());
        INIT_LOGGER.info("----> Remote-Host name: {}", this.serviceInfo.getLoopbackHostName());
        INIT_LOGGER.info("---->");
        INIT_LOGGER.info("----> External service URI: {}", this.serviceInfo.getExternalServiceURI());
        INIT_LOGGER.info("----> Remote-Host name: {}", this.serviceInfo.getLoopbackHostName());

        INIT_LOGGER.info("---->");
        INIT_LOGGER.info("----> Setup: {}",
                this.serviceInfo.isDistributed() ? "distributed / cloud setup" : "single host setup");
        INIT_LOGGER.info("----> Bundle: {}",
                this.serviceInfo.isSEBServerBundle() ? "SEB Server bundle" : "stand alone");

        INIT_LOGGER.info("---->");
        INIT_LOGGER.info("----> *********************************************************");
        INIT_LOGGER.info("----> *** Webservice successfully started up!               ***");
        INIT_LOGGER.info("----> *********************************************************");

    }

    private boolean registerWebservice() {
        boolean registered = false;
        try {
            registered = this.serviceInfo.registerWebservice();
            if (registered) {
                INIT_LOGGER.info("----> Successfully register Webservice instance. uuid: {}, address: {}",
                        this.serviceInfo.getWebserviceUUID(),
                        this.serviceInfo.getLocalHostAddress());
            }
        } catch (final Exception e) {
            INIT_LOGGER.error("----> Failed to register webservice: ", e);
        }
        return registered;
    }

    @PreDestroy
    public void gracefulShutdown() {
        INIT_LOGGER.info("*********************************************************");
        INIT_LOGGER.info("**** Gracefully Shutdown of SEB Server instance {}",
                this.serviceInfo.getHostAddress());
        INIT_LOGGER.info("---->");
        INIT_LOGGER.info("----> Unregister Webservice: {}", this.serviceInfo.getWebserviceUUID());

        try {
            this.serviceInfo.unregister();
        } catch (final Exception e) {
            INIT_LOGGER.error("Failed to unregister webservice: ", e);
        }

        INIT_LOGGER.info("---->");
        INIT_LOGGER.info("----> Webservice down");
    }

}
