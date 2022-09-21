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
import java.util.Set;

import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class ServiceInfo {

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

}
