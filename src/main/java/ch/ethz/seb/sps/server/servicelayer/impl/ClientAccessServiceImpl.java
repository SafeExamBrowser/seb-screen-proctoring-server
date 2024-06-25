/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import ch.ethz.seb.sps.server.datalayer.dao.ClientAccessDAO;
import ch.ethz.seb.sps.server.servicelayer.ClientAccessService;
import ch.ethz.seb.sps.server.weblayer.oauth.WebserviceResourceConfiguration;
import ch.ethz.seb.sps.utils.Constants;
import ch.ethz.seb.sps.utils.Cryptor;
import ch.ethz.seb.sps.utils.Result;
import ch.ethz.seb.sps.utils.Utils;

@Lazy
@Service
public class ClientAccessServiceImpl implements ClientAccessService {

    private final int sessionAccessTokenValSec;
    private final ClientAccessDAO clientAccessDAO;
    private final PasswordEncoder clientPasswordEncoder;
    private final Cryptor cryptor;

    public ClientAccessServiceImpl(
            final ClientAccessDAO clientAccessDAO,
            final PasswordEncoder clientPasswordEncoder,
            final Cryptor cryptor,
            @Value("${sps.api.session.accessTokenValiditySeconds:43200}") final int sessionAccessTokenValSec) {

        this.sessionAccessTokenValSec = sessionAccessTokenValSec;
        this.clientAccessDAO = clientAccessDAO;
        this.clientPasswordEncoder = clientPasswordEncoder;
        this.cryptor = cryptor;
    }

    @Override
    public Result<ClientDetails> getClientDetails(final String clientName) {
        return this.clientAccessDAO
                .getEncodedClientPWD(clientName, true)
                .map(encodedSecret -> getClientDetails(clientName, encodedSecret));
    }

    private ClientDetails getClientDetails(
            final String clientName,
            final CharSequence encodedSecret) {

        final BaseClientDetails clientDetails = new BaseClientDetails(
                Utils.toString(clientName),
                WebserviceResourceConfiguration.SESSION_API_RESOURCE_ID,
                null,
                Constants.OAUTH2_GRANT_TYPE_CLIENT_CREDENTIALS,
                StringUtils.EMPTY);

        clientDetails.setScope(Collections.emptySet());
        clientDetails.setAccessTokenValiditySeconds(this.sessionAccessTokenValSec);
        clientDetails.setRefreshTokenValiditySeconds(-1); // not used, not expiring

        // Note: the encodedSecret is either internally encrypted or with the clientPasswordEncoder
        try {

            clientDetails.setClientSecret(Utils.toString(
                    this.clientPasswordEncoder.encode(
                            this.cryptor.decrypt(encodedSecret).getOrThrow())));

        } catch (final Exception e) {
            clientDetails.setClientSecret(Utils.toString(encodedSecret));
        }

        return clientDetails;
    }

}
