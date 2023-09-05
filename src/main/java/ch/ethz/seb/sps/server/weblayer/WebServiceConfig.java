/*
 * Copyright (c) 2018 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfiguration;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;

import ch.ethz.seb.sps.server.ServiceConfig;
import ch.ethz.seb.sps.server.weblayer.oauth.PreAuthProvider;
import ch.ethz.seb.sps.server.weblayer.oauth.SEBClientDetailsService;
import ch.ethz.seb.sps.server.weblayer.oauth.WebServiceUserDetails;
import ch.ethz.seb.sps.server.weblayer.oauth.WebserviceResourceConfiguration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;

@Configuration
@EnableWebSecurity
@Order(6)
@Import(DataSourceAutoConfiguration.class)
@SuppressWarnings("deprecation")
public class WebServiceConfig
        extends org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
        implements ErrorController {

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
    private SEBClientDetailsService webServiceClientDetails;
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

    @Override
    @Bean(AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManagerBean() throws Exception {
        final AuthenticationManager authenticationManagerBean = super.authenticationManagerBean();
        return authenticationManagerBean;
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(this.webServiceUserDetails)
                .passwordEncoder(this.userPasswordEncoder);

        auth.authenticationProvider(this.preAuthProvider);
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable()
                .headers().frameOptions().disable()
                .and()
                .csrf().disable();
    }

    @Bean
    protected ResourceServerConfiguration sebServerAdminAPIResources() throws Exception {
        return new AdminAPIResourceServerConfiguration(
                this.tokenStore,
                this.webServiceClientDetails,
                authenticationManagerBean(),
                this.adminAPIEndpoint,
                this.unauthorizedRedirect,
                this.adminAccessTokenValSec,
                this.adminRefreshTokenValSec);
    }

    @Bean
    protected ResourceServerConfiguration sebServerExamAPIResources() throws Exception {
        return new SessionAPIClientResourceServerConfiguration(
                this.tokenStore,
                this.webServiceClientDetails,
                authenticationManagerBean(),
                this.sessionAPIEndpoint,
                this.sessionAccessTokenValSec);
    }

    private static final class AdminAPIResourceServerConfiguration extends WebserviceResourceConfiguration {

        public AdminAPIResourceServerConfiguration(
                final TokenStore tokenStore,
                final SEBClientDetailsService webServiceClientDetails,
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
                final SEBClientDetailsService webServiceClientDetails,
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
                        log.info("Redirect to login after unauthorized request");
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
