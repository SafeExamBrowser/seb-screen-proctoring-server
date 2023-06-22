/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer;

public interface SessionServiceHealthControl {

    int getUploadHealthIndicator();

    int getDownloadHealthIndicator();

    int getStoreHealthIndicator();

    int getDataSourceHelathIndicator();

    int getOverallLoadIndicator();

}
