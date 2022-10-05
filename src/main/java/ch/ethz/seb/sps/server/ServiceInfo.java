/*
 * Copyright (c) 2019 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import ch.ethz.seb.sps.utils.Constants;

@Lazy
@Service
public class ServiceInfo {

    private static final Logger log = LoggerFactory.getLogger(ServiceInfo.class);

    private static final String VERSION_KEY = "seb.sps.version";

    private final String version;
    private final Set<String> activeProfiles;

    public ServiceInfo(final Environment environment) {
        this.version = environment.getRequiredProperty(VERSION_KEY);
        this.activeProfiles = new HashSet<>(Arrays.asList(environment.getActiveProfiles()));
    }

    public String getVersion() {
        return this.version;
    }

    public boolean hasProfile(final String profile) {
        return this.activeProfiles.contains(profile);
    }

    public String getLocalHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (final UnknownHostException e) {
            log.error("Failed to get local host name: {}", e.getMessage());
            return Constants.EMPTY_NOTE;
        }
    }

    public String getLocalHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (final UnknownHostException e) {
            log.error("Failed to get local host address: {}", e.getMessage());
            return Constants.EMPTY_NOTE;
        }
    }

    public String getLoopbackHostName() {
        return InetAddress.getLoopbackAddress().getHostName();
    }

    public String getLoopbackHostAddress() {
        return InetAddress.getLoopbackAddress().getHostAddress();
    }

}
