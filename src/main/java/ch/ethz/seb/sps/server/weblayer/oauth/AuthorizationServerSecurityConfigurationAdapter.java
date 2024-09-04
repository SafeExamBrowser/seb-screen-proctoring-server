package ch.ethz.seb.sps.server.weblayer.oauth;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configuration.ClientDetailsServiceConfiguration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpointHandlerMapping;

@Configuration
@Order(0)
@Import({ClientDetailsServiceConfiguration.class, AuthorizationServerEndpointsConfiguration.class})
public class AuthorizationServerSecurityConfigurationAdapter {

    @Autowired
    private List<AuthorizationServerConfigurer> configurers = Collections.emptyList();
    @Autowired
    private ClientDetailsService clientDetailsService;
    @Autowired
    private AuthorizationServerEndpointsConfiguration endpoints;

    @Autowired
    public void configure(ClientDetailsServiceConfigurer clientDetails) throws Exception {
        Iterator var2 = this.configurers.iterator();

        while(var2.hasNext()) {
            AuthorizationServerConfigurer configurer = (AuthorizationServerConfigurer)var2.next();
            configurer.configure(clientDetails);
        }

    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
    }

    @Autowired
    public void configure(HttpSecurity http) throws Exception {
        AuthorizationServerSecurityConfigurer configurer = new AuthorizationServerSecurityConfigurer();
        FrameworkEndpointHandlerMapping handlerMapping = this.endpoints.oauth2EndpointHandlerMapping();
        http.setSharedObject(FrameworkEndpointHandlerMapping.class, handlerMapping);
        this.configure(configurer);
        http.apply(configurer);
        String tokenEndpointPath = handlerMapping.getServletPath("/oauth/token");
        String tokenKeyPath = handlerMapping.getServletPath("/oauth/token_key");
        String checkTokenPath = handlerMapping.getServletPath("/oauth/check_token");
        if (!this.endpoints.getEndpointsConfigurer().isUserDetailsServiceOverride()) {
            UserDetailsService userDetailsService = (UserDetailsService)http.getSharedObject(UserDetailsService.class);
            this.endpoints.getEndpointsConfigurer().userDetailsService(userDetailsService);
        }

        ((HttpSecurity.RequestMatcherConfigurer)((HttpSecurity)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)http.authorizeRequests().antMatchers(new String[]{tokenEndpointPath})).fullyAuthenticated().antMatchers(new String[]{tokenKeyPath})).access(configurer.getTokenKeyAccess()).antMatchers(new String[]{checkTokenPath})).access(configurer.getCheckTokenAccess()).and()).requestMatchers().antMatchers(new String[]{tokenEndpointPath, tokenKeyPath, checkTokenPath})).and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
        http.setSharedObject(ClientDetailsService.class, this.clientDetailsService);
    }

    protected void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        Iterator var2 = this.configurers.iterator();

        while(var2.hasNext()) {
            AuthorizationServerConfigurer configurer = (AuthorizationServerConfigurer)var2.next();
            configurer.configure(oauthServer);
        }

    }
}
