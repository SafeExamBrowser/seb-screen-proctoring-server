/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final boolean initializeAdmin;
    private final String adminName;

    private final boolean initializeSEBServerAPIAccount;
    private final Environment environment;

    public AdminUserInitializer(
            final UserDAO userDAO,
            final Environment environment,
            final PasswordEncoder passwordEncoder,
            @Value("${sebserver.init.adminaccount.gen-on-init:false}") final boolean initializeAdmin,
            @Value("${sebserver.init.adminaccount.username:seb-server-admin}") final String adminName,
            @Value("${sps.init.sebserveraccount.generate:true}") final boolean initializeSEBServerAPIAccount) {

        this.userDAO = userDAO;
        this.environment = environment;
        this.passwordEncoder = passwordEncoder;
        this.initializeAdmin = initializeAdmin;
        this.adminName = adminName;
        this.initializeSEBServerAPIAccount = initializeSEBServerAPIAccount;
    }

    void initAccounts() {
        if (this.initializeAdmin) {
            initAdminAccount();
        }
        if (this.initializeSEBServerAPIAccount) {
            initializeSEBServerAPIAccount();
        }
    }

    private void initAdminAccount() {

        try {

            log.debug("Create initial admin account is switched on. Check database if exists...");
            final Result<ServerUser> byUsername = this.userDAO.byUsername(this.adminName);
            if (byUsername.hasValue()) {

                log.debug("Initial admin account already exists. Check if the password must be reset...");

                final ServerUser sebServerUser = byUsername.get();
                final String password = sebServerUser.getPassword();
                if (this.passwordEncoder.matches("admin", password)) {

                    log.debug("Setting new generated password for already existing admin account");
                    final CharSequence generateAdminPassword = this.generateAdminPassword();
                    if (generateAdminPassword != null) {
                        this.userDAO.changePassword(
                                sebServerUser.getUserInfo().getModelId(),
                                generateAdminPassword);
                        this.writeAdminCredentials(this.adminName, generateAdminPassword);
                    }
                }
            } else {
                final CharSequence generateAdminPassword = this.generateAdminPassword();

                this.userDAO.createNew(new UserMod(
                        this.adminName,
                        this.adminName,
                        this.adminName,
                        this.adminName,
                        generateAdminPassword,
                        generateAdminPassword,
                        null,
                        null,
                        null,
                        new HashSet<>(Arrays.asList(API.UserRole.ADMIN.name()))))
                        .getOrThrow();
            }
        } catch (final Exception e) {
            ServiceInit.INIT_LOGGER.error("---->");
            ServiceInit.INIT_LOGGER.error("----> SEB Server initial admin-account creation failed: ", e);
            ServiceInit.INIT_LOGGER.error("---->");
        }
    }

    private void writeAdminCredentials(final String name, final CharSequence pwd) {
        ServiceInit.INIT_LOGGER.info("---->");
        ServiceInit.INIT_LOGGER.info(
                "----> ******************************************************************************************"
                        + "*****************************************************************************");
        ServiceInit.INIT_LOGGER.info("----> SEB Server initial admin-account; name: {}, pwd: {}", name, pwd);
        ServiceInit.INIT_LOGGER.info("---->");
        ServiceInit.INIT_LOGGER.info(
                "----> !!!! NOTE: Do not forget to login and reset the generated admin password immediately !!!!");
        ServiceInit.INIT_LOGGER.info(
                "----> ******************************************************************************************"
                        + "*****************************************************************************");
        ServiceInit.INIT_LOGGER.info("---->");
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

            if (sebServerAPIAccountName == null || sebServerAPIAccountPassword == null) {
                log.warn("Missing ENV settings to createSEB Server API account. Skip creation");
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
                    new HashSet<>(List.of(API.UserRole.ADMIN.name()))))
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
