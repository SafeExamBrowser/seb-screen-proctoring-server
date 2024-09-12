/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import ch.ethz.seb.sps.server.weblayer.oauth.LoginRedirectOnUnauthorized;
import ch.ethz.seb.sps.server.weblayer.oauth.authserver.WebServiceUserDetails;
import org.apache.catalina.filters.RemoteIpFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Import(DataSourceAutoConfiguration.class)
public class WebServiceConfig implements ErrorController {

    /** Spring bean name of single AuthenticationManager bean */
    public static final String AUTHENTICATION_MANAGER = "AUTHENTICATION_MANAGER";

    @Autowired
    private WebServiceUserDetails webServiceUserDetails;
    @Autowired
    private PasswordEncoder userPasswordEncoder;

    @Value("${server.error.path}")
    private String errorPath;
    @Value("${sps.api.admin.endpoint}")
    private String adminAPIEndpoint;
    @Value("${sps.api.session.endpoint}")
    private String sessionAPIEndpoint;
    @Value("${sps.http.redirect}")
    private String unauthorizedRedirect;

    @Value("${sps.api.admin.accessTokenValiditySeconds:3600}")
    private Integer adminAccessTokenValSec;
    @Value("${sps.api.admin.refreshTokenValiditySeconds:-1}")
    private Integer adminRefreshTokenValSec;
    @Value("${sps.api.session.accessTokenValiditySeconds:43200}")
    private Integer sessionAccessTokenValSec;

    /** Used to get real remote IP address by using "X-Forwarded-For" and "X-Forwarded-Proto" header.
     * https://tomcat.apache.org/tomcat-7.0-doc/api/org/apache/catalina/filters/RemoteIpFilter.html
     *
     * @return RemoteIpFilter instance */
    @Bean
    public RemoteIpFilter remoteIpFilter() {
        return new RemoteIpFilter();
    }

    @Bean
    @Order(6)
    public SecurityFilterChain overallFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/**").permitAll()
                        .anyRequest()
                        .authenticated())
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .headers(c -> c.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .exceptionHandling( c -> c.authenticationEntryPoint(new LoginRedirectOnUnauthorized()))
        ;
        
        return http.build();
    }
    
}
