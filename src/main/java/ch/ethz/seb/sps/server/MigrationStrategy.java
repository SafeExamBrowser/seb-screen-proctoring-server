/*
 * Copyright (c) 2021 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import org.flywaydb.core.api.output.ValidateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.stereotype.Component;

@Component
public class MigrationStrategy implements FlywayMigrationStrategy {

    private static final Logger log = LoggerFactory.getLogger(MigrationStrategy.class);

    private final boolean cleanDBOnStartup;
    private final ServiceInfo serviceInfo;
    private Flyway flyway;

    public MigrationStrategy(
            final ServiceInfo serviceInfo,
            @Value("${seb.sps.clean-db-on-startup:false}") final boolean cleanDBOnStartup) {

        this.serviceInfo = serviceInfo;
        this.cleanDBOnStartup = cleanDBOnStartup;
    }

    @Override
    public void migrate(final Flyway flyway) {
        this.flyway = flyway;
    }

    public void applyMigration() {
        if (this.serviceInfo.hasProfile("test")) {
            ServiceInit.INIT_LOGGER.info("No migration applies for test profile");
            return;
        }

        try {

            ServiceInit.INIT_LOGGER.info("----> **** Migration check START ******************************");
            ServiceInit.INIT_LOGGER.info("---->");
            ServiceInit.INIT_LOGGER.info("----> Check database status");

            final MigrationInfoService info = this.flyway.info();
            if (ServiceInit.INIT_LOGGER.isDebugEnabled()) {
                ServiceInit.INIT_LOGGER.debug("----> ** Migration Info **");
                ServiceInit.INIT_LOGGER.debug("----> {}", info);
            }

            final MigrationInfo[] pendingMigrations = info.pending();
            if (pendingMigrations != null && pendingMigrations.length > 0) {

                ServiceInit.INIT_LOGGER.info("----> Found pending migrations: {}", pendingMigrations.length);

                doMigration();

            } else {
                ServiceInit.INIT_LOGGER.info("----> ");
                ServiceInit.INIT_LOGGER.info("----> No pending migrations found. Last migration --> {} --> {}",
                        info.current().getVersion(),
                        info.current().getDescription());

                // TODO add this also for SEB Server
                final ValidateResult validateWithResult = this.flyway.validateWithResult();
                if (!validateWithResult.validationSuccessful) {
                    ServiceInit.INIT_LOGGER.warn("----> ");
                    ServiceInit.INIT_LOGGER.warn("----> VALIDATION OF DB MIGRATION SCHEMA FAILED:  \n\n" +
                            validateWithResult.getAllErrorMessages() + "\n\n");
                }
            }

            ServiceInit.INIT_LOGGER.info("---->");
            ServiceInit.INIT_LOGGER.info("----> **** Migration check END ********************************");
        } catch (final Exception e) {
            log.error("Failed to apply migration task: ", e);
        }
    }

    private void doMigration() {

        ServiceInit.INIT_LOGGER.info("----> *********************************************************");
        ServiceInit.INIT_LOGGER.info("----> **** Start Migration ************************************");

        if (this.cleanDBOnStartup) {

            ServiceInit.INIT_LOGGER
                    .info("----> !!! Cleanup database as it was set on sebserver.webservice.clean-db-on-startup !!!");

            this.flyway.clean();
        }

        // repair checksum mismatch if needed
        final ValidateResult validateWithResult = this.flyway.validateWithResult();
        if (!validateWithResult.validationSuccessful
                && validateWithResult.getAllErrorMessages().contains("checksum mismatch")) {

            ServiceInit.INIT_LOGGER.info("----> Migration validation checksum mismatch error detected: ");
            ServiceInit.INIT_LOGGER.info("----> {}", validateWithResult.getAllErrorMessages());
            ServiceInit.INIT_LOGGER.info("----> Try to run repair task...");

            this.flyway.repair();

        }

        this.flyway.migrate();

        final MigrationInfoService info = this.flyway.info();
        ServiceInit.INIT_LOGGER.info("----> Migration finished, new current version is: {} --> {}",
                info.current().getVersion(),
                info.current().getDescription());

        ServiceInit.INIT_LOGGER.info("----> **** End Migration **************************************");
        ServiceInit.INIT_LOGGER.info("----> *********************************************************");
    }

}
