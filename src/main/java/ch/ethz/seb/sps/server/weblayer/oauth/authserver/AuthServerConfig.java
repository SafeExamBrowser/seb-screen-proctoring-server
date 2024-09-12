/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer.oauth.authserver;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import ch.ethz.seb.sps.server.ServiceInfo;
import ch.ethz.seb.sps.server.datalayer.dao.UserDAO;
import ch.ethz.seb.sps.server.servicelayer.SEBClientAccessService;
import ch.ethz.seb.sps.server.weblayer.oauth.authserver.pwdgrant.OAuth2PasswordGrantAuthenticationConverter;
import ch.ethz.seb.sps.server.weblayer.oauth.authserver.pwdgrant.OAuth2PasswordGrantAuthenticationProvider;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
@EnableWebSecurity
public class AuthServerConfig {

    @Autowired
    private SEBClientAccessService sebClientAccessService;
    @Autowired
    private RegisteredGuiClient registeredGuiClient;
    @Autowired
    private RegisteredSEBServerClient registeredSEBServerClient;
    @Autowired
    private WebServiceUserDetails webServiceUserDetails;
    @Autowired
    private UserDAO userDAO;
    @Value("${sps.http.redirect}")
    private String unauthorizedRedirect;
    @Autowired
    private ServiceInfo serviceInfo;
    @Autowired
    private PasswordEncoder userPasswordEncoder;


    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(
            HttpSecurity http,
            AuthenticationManager authenticationManager,
            OAuth2AuthorizationService authorizationService) throws Exception {
        
       OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
       
        OAuth2PasswordGrantAuthenticationProvider resourceOwnerPasswordAuthenticationProvider =
                new OAuth2PasswordGrantAuthenticationProvider(authenticationManager, authorizationService);
        OAuth2ClientCredentialsGrantProvider oAuth2ClientCredentialsGrantProvider =
                new OAuth2ClientCredentialsGrantProvider(authorizationService);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .authorizationEndpoint(c -> c.authenticationProviders( providers -> {
                    providers.add(0, oAuth2ClientCredentialsGrantProvider);
                    providers.add(0, resourceOwnerPasswordAuthenticationProvider);
                }));

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .tokenEndpoint( e -> e.accessTokenRequestConverter(
                        new OAuth2PasswordGrantAuthenticationConverter()));
        http
                // Redirect to the login page when not authenticated from the
                // authorization endpoint
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint(unauthorizedRedirect),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                );
        

        DefaultSecurityFilterChain result = http.build();
        
        // we have to initialize the custom providers after the chain has been built
        resourceOwnerPasswordAuthenticationProvider.init(http);
        oAuth2ClientCredentialsGrantProvider.init(http);
        
        return result;
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(this.webServiceUserDetails)
                .passwordEncoder(this.userPasswordEncoder);
        //authenticationManagerBuilder.authenticationProvider(authProvider);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public UserDetailsManager userDetailsManager() {
        return new UserDetailsManagerImpl(userDAO);
    }

    @Bean
    public OAuth2AuthorizationService oAuth2AuthorizationService() {
        return new InMemoryTokenStore();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        return new RegisteredClientRepositoryImpl(
                sebClientAccessService, 
                registeredGuiClient, 
                registeredSEBServerClient);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings
                .builder()
                .tokenEndpoint("/oauth/token")
                .tokenRevocationEndpoint("/oauth/revoke-token")
                .build();
    }

}
