/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import ch.ethz.seb.sps.server.datalayer.dao.ClientAccessDAO;
import ch.ethz.seb.sps.server.servicelayer.ClientAccessService;
import ch.ethz.seb.sps.server.weblayer.oauth.WebserviceResourceConfiguration;
import ch.ethz.seb.sps.utils.Constants;
import ch.ethz.seb.sps.utils.Result;
import ch.ethz.seb.sps.utils.Utils;

@Service
public class ClientAccessServiceImpl implements ClientAccessService {

    private final int sessionAccessTokenValSec;
    private final ClientAccessDAO clientAccessDAO;

    public ClientAccessServiceImpl(
            @Value("${sps.api.session.accessTokenValiditySeconds:43200}") final int sessionAccessTokenValSec,
            final ClientAccessDAO clientAccessDAO) {

        this.sessionAccessTokenValSec = sessionAccessTokenValSec;
        this.clientAccessDAO = clientAccessDAO;
    }

    @Override
    public Result<ClientDetails> getClientDetails(final String clientName) {
        return this.clientAccessDAO
                .getEncodedClientPWD(clientName)
                .map(encodedSecret -> getClientDetails(clientName, encodedSecret));
    }

    private ClientDetails getClientDetails(final String clientName, final CharSequence encodedSecret) {
        final BaseClientDetails clientDetails = new BaseClientDetails(
                Utils.toString(clientName),
                WebserviceResourceConfiguration.SESSION_API_RESOURCE_ID,
                null,
                Constants.OAUTH2_GRANT_TYPE_CLIENT_CREDENTIALS,
                StringUtils.EMPTY);

        clientDetails.setScope(Collections.emptySet());
        clientDetails.setClientSecret(Utils.toString(encodedSecret));
        clientDetails.setAccessTokenValiditySeconds(this.sessionAccessTokenValSec);
        clientDetails.setRefreshTokenValiditySeconds(-1); // not used, not expiring

        return clientDetails;
    }

}
