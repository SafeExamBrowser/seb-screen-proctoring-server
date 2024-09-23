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
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SEBClientResourceServerConfig {

    private static final Logger log = LoggerFactory.getLogger(AdminAPIResourceServerConfig.class);

    @Autowired
    private JwtDecoder jwtDecoder;
    @Value("${server.error.path}")
    private String errorPath;
    @Value("${sps.api.session.endpoint}")
    private String sessionAPIEndpoint;
    @Value("${sps.http.redirect}")
    private String unauthorizedRedirect;

    @Bean
    @Order(2)
    SecurityFilterChain sebClientResourceFilterChain(HttpSecurity http) throws Exception {
        
        http.securityMatcher(sessionAPIEndpoint + "/**")
                .authorizeHttpRequests((requests) -> requests
                        .anyRequest().authenticated())
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .headers(c -> c.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .exceptionHandling( c -> c.authenticationEntryPoint(new WebConfig.UnauthoritedRequestHandler("SEBClientResourceServerConfig")))
        ;

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new SEBAPIJwtGrantedAuthoritiesConverter());
        http
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(new WebConfig.UnauthoritedRequestHandler("SEBClientResourceServerConfig"))
                        .jwt(jwt -> jwt.decoder(jwtDecoder).jwtAuthenticationConverter(jwtAuthenticationConverter))
                );

        return http.build();
    }


    private static class SEBAPIJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

        private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        @Override
        public Collection<GrantedAuthority> convert(@NotNull final Jwt source) {
            final List<String> scopes;
            try {
                scopes = source.getClaim("scope");
            } catch (Exception e) {
                log.warn("Failed to extract scope claim from JWT: {}", source);
                throw new InvalidBearerTokenException("Invalid scope");
            }
            if (!scopes.contains(API.SEB_API_SCOPE_NAME)) {
                throw new InvalidBearerTokenException("Invalid scope");
            }

            return jwtGrantedAuthoritiesConverter.convert(source);
        }
    }
}
