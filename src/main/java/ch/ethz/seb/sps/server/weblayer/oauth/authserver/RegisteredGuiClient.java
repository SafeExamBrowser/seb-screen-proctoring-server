/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer.oauth.authserver;

import static java.time.temporal.ChronoUnit.*;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class RegisteredGuiClient {
    
        public final RegisteredClient client;

        public RegisteredGuiClient(
            final PasswordEncoder clientPasswordEncoder,
            @Value("${sps.api.admin.gui.clientId}") final String clientId,
            @Value("${sps.api.admin.gui.clientSecret}") final String clientSecret,
            @Value("${sps.api.admin.accessTokenValiditySeconds:3600}") final Integer accessTokenValiditySeconds,
            @Value("${sps.api.admin.refreshTokenValiditySeconds:-1}") final Integer refreshTokenValiditySeconds) {

            Duration refreshTokenValDuration = (refreshTokenValiditySeconds == null || refreshTokenValiditySeconds.longValue() < 0)
                    ? Duration.of(1, YEARS)
                    : Duration.of(refreshTokenValiditySeconds, SECONDS);

            client = RegisteredClient
                    .withId(clientId)
                    .clientId(clientId)
                    .clientSecret(clientPasswordEncoder.encode(clientSecret))
                    .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                    .clientSecretExpiresAt(null)
                    .scope("read")
                    .scope("write")
                    .tokenSettings(TokenSettings
                            .builder()
                            .accessTokenTimeToLive(Duration.of(accessTokenValiditySeconds, SECONDS))
                            .refreshTokenTimeToLive(refreshTokenValDuration)
                            .build())
                    .build();
    }

    public String getClientId() {
            return client.getClientId();
    }
}
