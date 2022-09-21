/*
 * Copyright (c) 2019 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server;

import java.util.Arrays;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.user.ServerUser;
import ch.ethz.seb.sps.domain.model.user.UserMod;
import ch.ethz.seb.sps.server.servicelayer.dao.UserDAO;
import ch.ethz.seb.sps.utils.Result;
import ch.ethz.seb.sps.utils.client.ClientCredentialServiceImpl;

@Component
class AdminUserInitializer {

    private static final Logger log = LoggerFactory.getLogger(AdminUserInitializer.class);

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    private final boolean initializeAdmin;
    private final String adminName;

    public AdminUserInitializer(
            final UserDAO userDAO,
            @Qualifier(ServiceConfig.USER_PASSWORD_ENCODER_BEAN_NAME) final PasswordEncoder passwordEncoder,
            @Value("${sebserver.init.adminaccount.gen-on-init:false}") final boolean initializeAdmin,
            @Value("${sebserver.init.adminaccount.username:seb-server-admin}") final String adminName) {

        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
        this.initializeAdmin = initializeAdmin;
        this.adminName = adminName;
    }

    void initAdminAccount() {
        if (!this.initializeAdmin) {
            log.debug("Create initial admin account is switched on off");
            return;
        }

        try {

            log.debug("Create initial admin account is switched on. Check database if exists...");
            final Result<ServerUser> byUsername = this.userDAO.sebServerAdminByUsername(this.adminName);
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
                        new HashSet<>(Arrays.asList(API.UserRoles.ADMIN.name()))))
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

}
