/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server;

import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.user.ServerUser;
import ch.ethz.seb.sps.domain.model.user.UserMod;
import ch.ethz.seb.sps.server.datalayer.dao.UserDAO;
import ch.ethz.seb.sps.server.servicelayer.impl.ClientCredentialServiceImpl;
import ch.ethz.seb.sps.utils.Result;

@Component
class AdminUserInitializer {

    private static final Logger log = LoggerFactory.getLogger(AdminUserInitializer.class);

    private final UserDAO userDAO;
    private final boolean initializeSEBServerAPIAccount;
    private final Environment environment;

    public AdminUserInitializer(
            final UserDAO userDAO,
            final Environment environment,
            @Value("${sps.init.sebserveraccount.generate:true}") final boolean initializeSEBServerAPIAccount) {

        this.userDAO = userDAO;
        this.environment = environment;
        this.initializeSEBServerAPIAccount = initializeSEBServerAPIAccount;
    }

    void initAccounts() {
        if (this.initializeSEBServerAPIAccount) {
            initializeSEBServerAPIAccount();
        }
    }

    private CharSequence generateAdminPassword() {
        try {
            return ClientCredentialServiceImpl.generateClientSecret();
        } catch (final Exception e) {
            log.error("Unable to generate admin password: ", e);
            throw e;
        }
    }

    private void initializeSEBServerAPIAccount() {
        try {

            final String sebServerAPIAccountName =
                    this.environment.getProperty("sps.init.sebserveraccount.username", (String) null);
            final String sebServerAPIAccountPassword =
                    this.environment.getProperty("sps.init.sebserveraccount.password", (String) null);

            if (StringUtils.isBlank(sebServerAPIAccountName)) {
                log.error("No initial SEB Server API account defined");
                throw new IllegalArgumentException("No initial SEB Server API account defined");
            }

            if (StringUtils.isBlank(sebServerAPIAccountPassword)) {
                log.error("No initial SEB Server API account password defined");
                throw new IllegalArgumentException("No initial SEB Server API account password defined");
            }

            log.debug("Create initial SEB Server API account is switched on. Check database if exists...");
            final Result<ServerUser> byUsername = this.userDAO.byUsername(sebServerAPIAccountName);
            if (byUsername.hasValue()) {
                log.debug(
                        "SEB Server API account with name: {} already exists. Skip creation",
                        sebServerAPIAccountName);
                return;
            }

            ServiceInit.INIT_LOGGER.info("---->");
            ServiceInit.INIT_LOGGER.info("----> Create Initial SEB Server API Account with name: {}",
                    sebServerAPIAccountName);
            ServiceInit.INIT_LOGGER.info("---->");

            if (StringUtils.isBlank(sebServerAPIAccountName)) {
                log.error("------> Initial SEB Server API Account Name is not set in: sps.init.sebserveraccount.username");
                throw new IllegalArgumentException("Initial SEB Server API Account Name is not set in: sps.init.sebserveraccount.username");
            }
            if (StringUtils.isBlank(sebServerAPIAccountPassword)) {
                log.error("------> Initial SEB Server API Account Name is not set in: sps.init.sebserveraccount.password");
                throw new IllegalArgumentException("Initial SEB Server API Account Name is not set in: sps.init.sebserveraccount.password");
            }

            this.userDAO.createNew(new UserMod(
                    sebServerAPIAccountName,
                    sebServerAPIAccountName,
                    sebServerAPIAccountName,
                    sebServerAPIAccountName,
                    sebServerAPIAccountPassword,
                    sebServerAPIAccountPassword,
                    null,
                    null,
                    null,
                    new HashSet<>(List.of(API.UserRole.ADMIN.name())),
                            1L))
                    .getOrThrow();

            log.debug(
                    "Initial SEB Server API account with name: {} successfully created",
                    sebServerAPIAccountName);

        } catch (final Exception e) {
            ServiceInit.INIT_LOGGER.error("---->");
            ServiceInit.INIT_LOGGER.error("----> SEB Server initial SEB Server API Account creation failed: ", e);
            ServiceInit.INIT_LOGGER.error("---->");
        }
    }

}
