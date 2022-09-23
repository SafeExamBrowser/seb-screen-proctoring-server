/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.stereotype.Component;

import ch.ethz.seb.sps.server.servicelayer.ClientAccessService;
import ch.ethz.seb.sps.utils.Result;

@Component
public class ClientAccessServiceImpl implements ClientAccessService {

    @Override
    public Result<ClientDetails> getClientConfigDetails(final String clientName) {
        // TODO Auto-generated method stub
        return null;
    }

}
