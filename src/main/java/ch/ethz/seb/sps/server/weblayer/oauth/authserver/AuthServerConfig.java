/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer.oauth.authserver;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.server.datalayer.dao.UserDAO;
import ch.ethz.seb.sps.server.servicelayer.SEBClientAccessService;
import ch.ethz.seb.sps.server.weblayer.oauth.authserver.pwdgrant.OAuth2PasswordGrantAuthenticationConverter;
import ch.ethz.seb.sps.server.weblayer.oauth.authserver.pwdgrant.OAuth2PasswordGrantAuthenticationProvider;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

@Configuration
@EnableWebSecurity
public class AuthServerConfig {

    @Autowired
    private PasswordEncoder userPasswordEncoder;
    @Autowired
    private AutoLoginService autoLoginService;
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
    
    
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(
            HttpSecurity http,
            AuthenticationManager authenticationManager,
            OAuth2AuthorizationService authorizationService) throws Exception {
        
       OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        OAuth2PasswordGrantAuthenticationProvider oAuth2PasswordGrantAuthenticationProvider =
                new OAuth2PasswordGrantAuthenticationProvider(authenticationManager, authorizationService);
        OAuth2ClientCredentialsGrantProvider oAuth2ClientCredentialsGrantProvider =
                new OAuth2ClientCredentialsGrantProvider(authorizationService);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .authorizationEndpoint(c -> c.authenticationProviders( providers -> {
                    providers.add(0, oAuth2ClientCredentialsGrantProvider);
                    providers.add(0, oAuth2PasswordGrantAuthenticationProvider);
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
        oAuth2PasswordGrantAuthenticationProvider.init(http);
        oAuth2ClientCredentialsGrantProvider.init(http);
        autoLoginService.init(oAuth2PasswordGrantAuthenticationProvider);
        
        return result;
    }
    
    @Bean
    public PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider() {
        PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider = 
                new PreAuthenticatedAuthenticationProvider();
        preAuthenticatedAuthenticationProvider.setPreAuthenticatedUserDetailsService(webServiceUserDetails);
        return preAuthenticatedAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(this.webServiceUserDetails)
                .passwordEncoder(this.userPasswordEncoder);
        return authenticationManagerBuilder.build();
    }
    
    @Bean
    public BearerTokenResolver customBearerTokenResolver() {
        return new CustomBearerTokenResolver();
    }

    @Bean
    public UserDetailsManager userDetailsManager() {
        return new UserDetailsManagerImpl(userDAO);
    }

    @Bean
    public OAuth2AuthorizationService oAuth2AuthorizationService() {
        return new DummyTokenStore();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        return new RegisteredClientRepositoryImpl(
                sebClientAccessService, 
                registeredGuiClient, 
                registeredSEBServerClient);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings
                .builder()
                .tokenEndpoint(API.OAUTH_TOKEN_ENDPOINT)
                .tokenRevocationEndpoint(API.OAUTH_REVOKE_TOKEN_ENDPOINT)
                .build();
    }
    
    private static final class CustomBearerTokenResolver implements BearerTokenResolver {
        private final DefaultBearerTokenResolver delegate = new DefaultBearerTokenResolver();

        @Override
        public String resolve(HttpServletRequest request) {
            String token = delegate.resolve(request);
            if (token != null) {
                return token;
            }

            String accessToken = request.getParameter("access_token");
            return accessToken;
        }
    }

}
