/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.ethz.seb.sps.server.weblayer.oauth.*;
import org.apache.catalina.filters.RemoteIpFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.server.ServiceConfig;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

@Configuration
@EnableWebSecurity
@Import(DataSourceAutoConfiguration.class)
public class WebServiceConfig implements ErrorController {

    public static final String SWAGGER_AUTH_SEB_CLIENT = "SEBOAuth";
    public static final String SWAGGER_AUTH_GUI_ADMIN = "guiClient";
    public static final String SWAGGER_AUTH_SEBSEVER_ADMIN = "sebserverClient";

    private static final Logger log = LoggerFactory.getLogger(WebServiceConfig.class);

    /** Spring bean name of single AuthenticationManager bean */
    public static final String AUTHENTICATION_MANAGER = "AUTHENTICATION_MANAGER";

    @Autowired
    private WebServiceUserDetails webServiceUserDetails;
    @Autowired
    @Qualifier(ServiceConfig.USER_PASSWORD_ENCODER_BEAN_NAME)
    private PasswordEncoder userPasswordEncoder;
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private SPSClientDetailsService webServiceClientDetails;
    @Autowired
    private BasicAuthUserDetailService basicAuthUserDetailService;
    @Autowired
    private PreAuthProvider preAuthProvider;

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
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(SWAGGER_AUTH_GUI_ADMIN, new SecurityScheme()
                                .type(Type.OAUTH2)
                                .scheme("bearer")
                                .in(In.HEADER)
                                .bearerFormat("jwt")
                                .flows(new OAuthFlows().password(new OAuthFlow().tokenUrl("/oauth/token"))))

                        .addSecuritySchemes(SWAGGER_AUTH_SEB_CLIENT, new SecurityScheme()
                                .type(Type.OAUTH2)
                                .scheme("basic")
                                .in(In.HEADER)
                                .flows(new OAuthFlows().clientCredentials(new OAuthFlow()
                                        .tokenUrl("/oauth/token")
                                        .scopes(new Scopes().addString("read", "read").addString("write", "write"))))));

    }

    @Bean
    public AccessTokenConverter accessTokenConverter() {
        final DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(userAuthenticationConverter());
        return accessTokenConverter;
    }

    @Bean
    public UserAuthenticationConverter userAuthenticationConverter() {
        final DefaultUserAuthenticationConverter userAuthenticationConverter =
                new DefaultUserAuthenticationConverter();
        userAuthenticationConverter.setUserDetailsService(this.webServiceUserDetails);
        return userAuthenticationConverter;
    }
    
    @Bean(AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder authManagerBuilder =  http.getSharedObject(AuthenticationManagerBuilder.class);
        authManagerBuilder
                .userDetailsService(this.webServiceUserDetails)
                .passwordEncoder(this.userPasswordEncoder);
        authManagerBuilder
                .userDetailsService(this.basicAuthUserDetailService)
                .passwordEncoder(this.userPasswordEncoder);
        return authManagerBuilder.build();
    }

    @Bean
    @Order(6)
    public SecurityFilterChain overallFilterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable()
                .headers().frameOptions().disable()
                .and()
                .csrf()
                .disable();

        http
                .antMatcher(API.OAUTH_JWTTOKEN_ENDPOINT + "/**")
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .httpBasic();

        http
                .antMatcher(API.REGISTER_ENDPOINT)
                .authorizeRequests()
                .and()
                .httpBasic();
        http
                .securityContext((securityContext) -> securityContext
                        .requireExplicitSave(true)
                )
                .securityContext((securityContext) -> securityContext
                        .securityContextRepository(new DelegatingSecurityContextRepository(
                                new RequestAttributeSecurityContextRepository(),
                                new HttpSessionSecurityContextRepository()
                        ))
                );
        
        return http.build();
    }

//    @Override
//    public void configure(final HttpSecurity http) throws Exception {
//        http
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .formLogin().disable()
//                .httpBasic().disable()
//                .logout().disable()
//                .headers().frameOptions().disable()
//                .and()
//                .csrf()
//                .disable();
//
//        http
//                .antMatcher(API.OAUTH_JWTTOKEN_ENDPOINT + "/**")
//                .authorizeRequests()
//                .anyRequest().authenticated()
//                .and()
//                .httpBasic();
//
//        http
//                .antMatcher(API.REGISTER_ENDPOINT)
//                .authorizeRequests()
//                .and()
//                .httpBasic();
//    }
    

//    @Bean
//    @Order(2)
//    public SecurityFilterChain sebServerAdminAPIResourcesFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
//        new AdminAPIResourceServerConfiguration(
//                this.tokenStore,
//                this.webServiceClientDetails,
//                authenticationManager,
//                this.adminAPIEndpoint,
//                this.unauthorizedRedirect,
//                this.adminAccessTokenValSec,
//                this.adminRefreshTokenValSec).configure(http);
//        return http.build();
//    }
//
//    @Bean
//    @Order(3)
//    public SecurityFilterChain sebServerExamAPIResourcesFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
//        new SessionAPIClientResourceServerConfiguration(
//                this.tokenStore,
//                this.webServiceClientDetails,
//                authenticationManager,
//                this.sessionAPIEndpoint,
//                this.sessionAccessTokenValSec).configure(http);
//        return http.build();
//    }

    @Bean
    protected ResourceServerConfigurationAdapter sebServerAdminAPIResources(AuthenticationManager authenticationManager) throws Exception {
        return new AdminAPIResourceServerConfiguration(
                this.tokenStore,
                this.webServiceClientDetails,
                authenticationManager,
                this.adminAPIEndpoint,
                this.unauthorizedRedirect,
                this.adminAccessTokenValSec,
                this.adminRefreshTokenValSec);
    }

    @Bean
    protected ResourceServerConfigurationAdapter sebServerExamAPIResources(AuthenticationManager authenticationManager) throws Exception {
        return new SessionAPIClientResourceServerConfiguration(
                this.tokenStore,
                this.webServiceClientDetails,
                authenticationManager,
                this.sessionAPIEndpoint,
                this.sessionAccessTokenValSec);
    }

    private static final class AdminAPIResourceServerConfiguration extends WebserviceResourceConfiguration {

        public AdminAPIResourceServerConfiguration(
                final TokenStore tokenStore,
                final SPSClientDetailsService webServiceClientDetails,
                final AuthenticationManager authenticationManager,
                final String apiEndpoint,
                final String redirect,
                final int adminAccessTokenValSec,
                final int adminRefreshTokenValSec) {

            super(
                    tokenStore,
                    webServiceClientDetails,
                    authenticationManager,
                    new LoginRedirectOnUnauthorized(),
                    ADMIN_API_RESOURCE_ID,
                    apiEndpoint,
                    true,
                    2,
                    adminAccessTokenValSec,
                    adminRefreshTokenValSec);
        }
    }

    private static final class SessionAPIClientResourceServerConfiguration extends WebserviceResourceConfiguration {

        public SessionAPIClientResourceServerConfiguration(
                final TokenStore tokenStore,
                final SPSClientDetailsService webServiceClientDetails,
                final AuthenticationManager authenticationManager,
                final String apiEndpoint,
                final int adminAccessTokenValSec) {

            super(
                    tokenStore,
                    webServiceClientDetails,
                    authenticationManager,
                    (request, response, exception) -> {
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        log.warn("Unauthorized Request: {}", request, exception);
                        response.getOutputStream().println("{ \"error\": \"" + exception.getMessage() + "\" }");
                    },
                    SESSION_API_RESOURCE_ID,
                    apiEndpoint,
                    true,
                    3,
                    adminAccessTokenValSec,
                    1);
        }
    }

    private static class LoginRedirectOnUnauthorized implements AuthenticationEntryPoint {

        @Override
        public void commence(
                final HttpServletRequest request,
                final HttpServletResponse response,
                final AuthenticationException authenticationException) throws IOException {

            log.warn("Unauthorized Request on: {}", request.getRequestURI());

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.flushBuffer();
        }
    }

}
