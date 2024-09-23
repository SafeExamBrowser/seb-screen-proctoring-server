/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.Duration;

import ch.ethz.seb.sps.domain.api.API;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;

import ch.ethz.seb.sps.server.datalayer.dao.ClientAccessDAO;
import ch.ethz.seb.sps.server.servicelayer.SEBClientAccessService;
import ch.ethz.seb.sps.utils.Cryptor;
import ch.ethz.seb.sps.utils.Result;
import ch.ethz.seb.sps.utils.Utils;

@Lazy
@Service
public class SEBClientAccessServiceImpl implements SEBClientAccessService {

    private final int sessionAccessTokenValSec;
    private final ClientAccessDAO clientAccessDAO;
    private final PasswordEncoder clientPasswordEncoder;
    private final Cryptor cryptor;

    public SEBClientAccessServiceImpl(
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
    public Result<RegisteredClient> getClientDetails(final String clientName) {
        return this.clientAccessDAO
                .getEncodedClientPWD(clientName, true)
                .map(encodedSecret -> getRegisteredClient(clientName, encodedSecret));
    }

    private RegisteredClient getRegisteredClient(
            final String clientName,
            final CharSequence encodedSecret) {

        RegisteredClient.Builder builder = RegisteredClient
                .withId(clientName)
                .clientId(clientName)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .clientSecretExpiresAt(null)
                .scope(API.READ_SCOPE_NAME)
                .scope(API.WRITE_SCOPE_NAME)
                .scope(API.SEB_API_SCOPE_NAME)
                .tokenSettings(TokenSettings
                        .builder()
                        .accessTokenTimeToLive(Duration.of(this.sessionAccessTokenValSec, SECONDS))
                        .build());

        // Note: the encodedSecret is either internally encrypted or with the clientPasswordEncoder
        try {

            builder.clientSecret(Utils.toString(
                    this.clientPasswordEncoder.encode(
                            this.cryptor.decrypt(encodedSecret).getOrThrow())));

        } catch (final Exception e) {
            
            log.info("SEB Client secret fallback with secret: {}", encodedSecret);
            
            if (clientPasswordEncoder.upgradeEncoding(Utils.toString(encodedSecret))) {
                builder.clientSecret(clientPasswordEncoder.encode(Utils.toString(encodedSecret)));
            } else {
                builder.clientSecret(Utils.toString(encodedSecret));
            }
        }
        
        return builder.build();
    }

}
