/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer.basicauth;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.server.weblayer.WebConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Import(DataSourceAutoConfiguration.class)
public class BasicAuthServiceConfig implements ErrorController {

    /** Spring bean name of single AuthenticationManager bean */
    public static final String AUTHENTICATION_MANAGER = "AUTHENTICATION_MANAGER";
    
    @Autowired
    private PasswordEncoder userPasswordEncoder;
    @Autowired
    BasicAuthClientService basicAuthClientService;
    
    @Bean
    @Order(4)
    public SecurityFilterChain autologinFilterChain(HttpSecurity http) throws Exception {

        http.securityMatcher(API.OAUTH_JWTTOKEN_ENDPOINT + "/**")
                .authorizeHttpRequests((requests) -> requests
                .anyRequest().authenticated())
                .httpBasic(c -> c.authenticationEntryPoint(new WebConfig.UnauthoritedRequestHandler("Basic Auth")))
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .headers(c -> c.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(basicAuthClientService);
        auth.setPasswordEncoder(userPasswordEncoder);
        authenticationManagerBuilder.authenticationProvider(auth);
        
        return http.build();
    }
    
}
