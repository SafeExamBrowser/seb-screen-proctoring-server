/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer;

public interface SessionServiceHealthControl {

    int HEALTH_INDICATOR_MAX = 10;
    int BATCH_SIZE_INDICATOR_MAP_MAX = 800;
    int THREAD_POOL_SIZE_INDICATOR_MAP_MAX = 500;
    int THREAD_POOL_SIZE_INDICATOR_MAP_MIN = 100;

    int getUploadHealthIndicator();

    int getDownloadHealthIndicator();

    int getStoreHealthIndicator();

    int getDataSourceHealthIndicator();

    int getOverallLoadIndicator();

}
