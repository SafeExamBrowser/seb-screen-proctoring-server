/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer.oauth.resserver;

import java.util.Collection;
import java.util.List;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.server.weblayer.WebConfig;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class AdminAPIResourceServerConfig {

    private static final Logger log = LoggerFactory.getLogger(AdminAPIResourceServerConfig.class);
    
    @Autowired
    private JwtDecoder jwtDecoder;
    @Value("${sps.api.admin.endpoint}")
    private String adminAPIEndpoint;
    @Autowired
    private OAuth2AuthorizationService oAuth2AuthorizationService;

    @Bean
    @Order(3)
    SecurityFilterChain adminResourceSecurityFilterChain(HttpSecurity http) throws Exception {
        
        http.securityMatcher(adminAPIEndpoint + "/**")
                .authorizeHttpRequests((requests) -> requests
                        .anyRequest().authenticated())
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .headers(c -> c.frameOptions(HeadersConfigurer.FrameOptionsConfig::deny))
                .exceptionHandling( c -> c.authenticationEntryPoint(new WebConfig.UnauthorizedRequestHandler("AdminAPIResourceServerConfig")))
        ;

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new AdminAPIJwtGrantedAuthoritiesConverter(oAuth2AuthorizationService));
        http
                .oauth2ResourceServer(resServer -> resServer
                        .authenticationEntryPoint(new WebConfig.UnauthorizedRequestHandler("AdminAPIResourceServerConfig"))
                        .jwt(jwt -> jwt.decoder(jwtDecoder).jwtAuthenticationConverter(jwtAuthenticationConverter))
                );

        return http.build();
    }
    
    
    private static class AdminAPIJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

        private final OAuth2AuthorizationService oAuth2AuthorizationService;
        private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        private AdminAPIJwtGrantedAuthoritiesConverter(final OAuth2AuthorizationService oAuth2AuthorizationService) {
            this.oAuth2AuthorizationService = oAuth2AuthorizationService;
        }

        @Override
        public Collection<GrantedAuthority> convert(@NotNull final Jwt source) {

            // check token still available on store
            final OAuth2Authorization validAuth = oAuth2AuthorizationService.findByToken(source.getTokenValue(), OAuth2TokenType.ACCESS_TOKEN);
            if (validAuth == null) {
                throw new InvalidBearerTokenException("Invalid Bearer Token");
            }

            final List<String> scopes;
            try {
                scopes = source.getClaim("scope");
            } catch (Exception e) {
                log.warn("Failed to extract scope claim from JWT: {}", source);
                throw new InvalidBearerTokenException("Invalid scope");
            }
            if (!scopes.contains(API.WEB_API_SCOPE_NAME)) {
                throw new InvalidBearerTokenException("Invalid scope");
            }
            
            return jwtGrantedAuthoritiesConverter.convert(source);
        }
    }
    
}
