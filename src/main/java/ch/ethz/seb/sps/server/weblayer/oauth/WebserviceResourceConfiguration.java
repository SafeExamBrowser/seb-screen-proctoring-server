/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer.oauth;


/** Abstract Spring ResourceServerConfiguration to configure different resource services
 * for different API's. */
public abstract class WebserviceResourceConfiguration /* extends ResourceServerConfigurationAdapter */ {

    /** The resource identifier of Administration API resources */
    public static final String ADMIN_API_RESOURCE_ID = "sps-administration-api";
    /** The resource identifier of the session API resources */
    public static final String SESSION_API_RESOURCE_ID = "sps-session-api";

//    private final ConfigurerAdapter configurerAdapter;
//
//    public WebserviceResourceConfiguration(
//            final TokenStore tokenStore,
//            final SPSClientDetailsService webServiceClientDetails,
//            final AuthenticationManager authenticationManager,
//            final AuthenticationEntryPoint authenticationEntryPoint,
//            final String resourceId,
//            final String apiEndpoint,
//            final boolean supportRefreshToken,
//            final int order,
//            final int accessTokenValiditySeconds,
//            final int refreshTokenValiditySeconds) {
//
//        super();
//        configurerAdapter = new ConfigurerAdapter(
//                this,
//                tokenStore,
//                webServiceClientDetails,
//                authenticationManager,
//                authenticationEntryPoint,
//                resourceId,
//                apiEndpoint,
//                supportRefreshToken,
//                accessTokenValiditySeconds,
//                refreshTokenValiditySeconds);
//
//        setConfigurers(Arrays.asList(configurerAdapter));
//        super.setOrder(order);
//    }
//
////    public void configure(final HttpSecurity http) throws Exception {
////        ResourceServerSecurityConfigurer resources = new ResourceServerSecurityConfigurer();
////        resources.setBuilder(http);
////        resources.init(http);
////        addConfiguration(this.configurerAdapter, http);
////        resources.configure(http);
////        this.configurerAdapter.configure(resources);
////    }
//
//    protected void addConfiguration(final ConfigurerAdapter configurerAdapter, final HttpSecurity http)
//            throws Exception {
//        // To override of additional configuration is needed
//        http
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .antMatcher(configurerAdapter.apiEndpoint + "/**")
//                .authorizeRequests()
//                .anyRequest()
//                .authenticated()
//                .and()
//                .exceptionHandling()
//                .authenticationEntryPoint(configurerAdapter.authenticationEntryPoint)
//                .and()
//                .formLogin().disable()
//                .httpBasic().disable()
//                .logout().disable()
//                .headers().frameOptions().disable()
//                .and()
//                .csrf().disable();
//    }
//
//    protected static final class ConfigurerAdapter extends ResourceServerConfigurerAdapter {
//
//        public final WebserviceResourceConfiguration webserviceResourceConfiguration;
//        public final TokenStore tokenStore;
//        public final SPSClientDetailsService webServiceClientDetails;
//        public final AuthenticationManager authenticationManager;
//        public final AuthenticationEntryPoint authenticationEntryPoint;
//        public final String resourceId;
//        public final String apiEndpoint;
//        public final boolean supportRefreshToken;
//        public final int accessTokenValiditySeconds;
//        public final int refreshTokenValiditySeconds;
//
//        public ConfigurerAdapter(
//                final WebserviceResourceConfiguration webserviceResourceConfiguration,
//                final TokenStore tokenStore,
//                final SPSClientDetailsService webServiceClientDetails,
//                final AuthenticationManager authenticationManager,
//                final AuthenticationEntryPoint authenticationEntryPoint,
//                final String resourceId,
//                final String apiEndpoint,
//                final boolean supportRefreshToken,
//                final int accessTokenValiditySeconds,
//                final int refreshTokenValiditySeconds) {
//
//            super();
//            this.webserviceResourceConfiguration = webserviceResourceConfiguration;
//            this.tokenStore = tokenStore;
//            this.webServiceClientDetails = webServiceClientDetails;
//            this.authenticationManager = authenticationManager;
//            this.authenticationEntryPoint = authenticationEntryPoint;
//            this.resourceId = resourceId;
//            this.apiEndpoint = apiEndpoint;
//            this.supportRefreshToken = supportRefreshToken;
//            this.accessTokenValiditySeconds = accessTokenValiditySeconds;
//            this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
//        }
//
//        public void configure(final ResourceServerSecurityConfigurer resources) {
//            resources.resourceId(this.resourceId);
//            final DefaultTokenServices tokenService = new DefaultTokenServices();
//            tokenService.setTokenStore(this.tokenStore);
//            tokenService.setClientDetailsService(this.webServiceClientDetails);
//            tokenService.setSupportRefreshToken(this.supportRefreshToken);
//            tokenService.setReuseRefreshToken(false);
//            tokenService.setAuthenticationManager(this.authenticationManager);
//            tokenService.setAccessTokenValiditySeconds(this.accessTokenValiditySeconds);
//            tokenService.setRefreshTokenValiditySeconds(this.refreshTokenValiditySeconds);
//            resources.tokenServices(tokenService);
//        }
//
//    }

}
