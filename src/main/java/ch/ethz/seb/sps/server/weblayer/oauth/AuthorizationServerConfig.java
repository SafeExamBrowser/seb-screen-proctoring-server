/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

//package ch.ethz.seb.sps.server.weblayer.oauth;
//
//import jakarta.servlet.http.HttpServletResponse;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
//import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
//import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
//
//import ch.ethz.seb.sps.server.ServiceConfig;
//import ch.ethz.seb.sps.server.weblayer.WebServiceConfig;
//import ch.ethz.seb.sps.utils.Constants;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@Import({AuthorizationServerEndpointsConfiguration.class, AuthorizationServerSecurityConfigurationAdapter.class})
//@Order(100)
//public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
//
//    private static final Logger log = LoggerFactory.getLogger(AuthorizationServerConfig.class);
//
//    @Autowired
//    private AccessTokenConverter accessTokenConverter;
//    @Autowired(required = true)
//    private TokenStore tokenStore;
//    @Autowired
//    private WebServiceUserDetails webServiceUserDetails;
//    @Autowired
//    private SPSClientDetailsService sebClientDetailsService;
//    @Autowired
//    @Qualifier(ServiceConfig.CLIENT_PASSWORD_ENCODER_BEAN_NAME)
//    private PasswordEncoder clientPasswordEncoder;
//    @Autowired
//    @Qualifier(WebServiceConfig.AUTHENTICATION_MANAGER)
//    private AuthenticationManager authenticationManager;
//    @Value("${sps.api.admin.accessTokenValiditySeconds:3600}")
//    private Integer adminAccessTokenValSec;
//    @Value("${sps.api.admin.refreshTokenValiditySeconds:-1}")
//    private Integer adminRefreshTokenValSec;
//
////    @Bean
////    @Order(100)
////    public SecurityFilterChain overallFilterChain(HttpSecurity http) throws Exception {
////        super.co
////    }
//
//    @Override
//    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) {
//        oauthServer
//                .tokenKeyAccess("permitAll()")
//                .checkTokenAccess("isAuthenticated()")
//                .passwordEncoder(this.clientPasswordEncoder)
//                .authenticationEntryPoint((request, response, exception) -> {
//                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                    log.warn(
//                            "Unauthorized Request: {}",
//                            exception != null ? exception.getMessage() : Constants.EMPTY_NOTE);
//                    response.getOutputStream().println("{ \"error\": \"" + exception.getMessage() + "\" }");
//                });
//    }
//
//    @Override
//    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
//       clients.withClientDetails(this.sebClientDetailsService);
//    }
//
//    @Override
//    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) {
//        final JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
//        jwtAccessTokenConverter.setAccessTokenConverter(this.accessTokenConverter);
//
//        final DefaultTokenServices defaultTokenServices = new DefaultTokenServicesFallback();
//        defaultTokenServices.setTokenStore(this.tokenStore);
//        defaultTokenServices.setAuthenticationManager(this.authenticationManager);
//        defaultTokenServices.setSupportRefreshToken(true);
//        defaultTokenServices.setReuseRefreshToken(false);
//        defaultTokenServices.setTokenEnhancer(jwtAccessTokenConverter);
//        defaultTokenServices.setAccessTokenValiditySeconds(this.adminAccessTokenValSec);
//        defaultTokenServices.setRefreshTokenValiditySeconds(this.adminRefreshTokenValSec);
//        defaultTokenServices.setClientDetailsService(this.sebClientDetailsService);
//
//        endpoints
//                .tokenStore(this.tokenStore)
//                .authenticationManager(this.authenticationManager)
//                .userDetailsService(this.webServiceUserDetails)
//                .accessTokenConverter(jwtAccessTokenConverter)
//                .tokenServices(defaultTokenServices);
//    }
//
//}
