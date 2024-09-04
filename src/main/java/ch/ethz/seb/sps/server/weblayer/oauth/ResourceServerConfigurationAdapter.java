package ch.ethz.seb.sps.server.weblayer.oauth;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpointHandlerMapping;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.ReflectionUtils;

@Configuration
public class ResourceServerConfigurationAdapter implements Ordered {
    private int order = 3;
    @Autowired(
            required = false
    )
    private TokenStore tokenStore;
    @Autowired(
            required = false
    )
    private AuthenticationEventPublisher eventPublisher;
    @Autowired(
            required = false
    )
    private Map<String, ResourceServerTokenServices> tokenServices;
    @Autowired
    private ApplicationContext context;
    private List<ResourceServerConfigurer> configurers = Collections.emptyList();
    @Autowired(
            required = false
    )
    private AuthorizationServerEndpointsConfiguration endpoints;

    public ResourceServerConfigurationAdapter() {
    }

    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Autowired(
            required = false
    )
    public void setConfigurers(List<ResourceServerConfigurer> configurers) {
        this.configurers = configurers;
    }

    public void configure(HttpSecurity http) throws Exception {
        ResourceServerSecurityConfigurer resources = new ResourceServerSecurityConfigurer();
        ResourceServerTokenServices services = this.resolveTokenServices();
        if (services != null) {
            resources.tokenServices(services);
        } else if (this.tokenStore != null) {
            resources.tokenStore(this.tokenStore);
        } else if (this.endpoints != null) {
            resources.tokenStore(this.endpoints.getEndpointsConfigurer().getTokenStore());
        }

        if (this.eventPublisher != null) {
            resources.eventPublisher(this.eventPublisher);
        }

        Iterator var4 = this.configurers.iterator();

        ResourceServerConfigurer configurer;
        while(var4.hasNext()) {
            configurer = (ResourceServerConfigurer)var4.next();
            configurer.configure(resources);
        }

        ((HttpSecurity)((HttpSecurity)http.authenticationProvider(new AnonymousAuthenticationProvider("default")).exceptionHandling().accessDeniedHandler(resources.getAccessDeniedHandler()).and()).sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()).csrf().disable();
        http.apply(resources);
        if (this.endpoints != null) {
            http.requestMatcher(new ResourceServerConfigurationAdapter.NotOAuthRequestMatcher(this.endpoints.oauth2EndpointHandlerMapping()));
        }

        var4 = this.configurers.iterator();

        while(var4.hasNext()) {
            configurer = (ResourceServerConfigurer)var4.next();
            configurer.configure(http);
        }

        if (this.configurers.isEmpty()) {
            ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)http.authorizeRequests().anyRequest()).authenticated();
        }

    }

    private ResourceServerTokenServices resolveTokenServices() {
        if (this.tokenServices != null && this.tokenServices.size() != 0) {
            if (this.tokenServices.size() == 1) {
                return (ResourceServerTokenServices)this.tokenServices.values().iterator().next();
            } else {
                if (this.tokenServices.size() == 2) {
                    Iterator<ResourceServerTokenServices> iter = this.tokenServices.values().iterator();
                    ResourceServerTokenServices one = (ResourceServerTokenServices)iter.next();
                    ResourceServerTokenServices two = (ResourceServerTokenServices)iter.next();
                    if (this.elementsEqual(one, two)) {
                        return one;
                    }
                }

                return (ResourceServerTokenServices)this.context.getBean(ResourceServerTokenServices.class);
            }
        } else {
            return null;
        }
    }

    private boolean elementsEqual(Object one, Object two) {
        if (one == two) {
            return true;
        } else {
            Object targetOne = this.findTarget(one);
            Object targetTwo = this.findTarget(two);
            return targetOne == targetTwo;
        }
    }

    private Object findTarget(Object item) {
        Object current = item;

        while(current instanceof Advised) {
            try {
                current = ((Advised)current).getTargetSource().getTarget();
            } catch (Exception var4) {
                Exception e = var4;
                ReflectionUtils.rethrowRuntimeException(e);
            }
        }

        return current;
    }

    private static class NotOAuthRequestMatcher implements RequestMatcher {
        private FrameworkEndpointHandlerMapping mapping;

        public NotOAuthRequestMatcher(FrameworkEndpointHandlerMapping mapping) {
            this.mapping = mapping;
        }

        public boolean matches(HttpServletRequest request) {
            String requestPath = this.getRequestPath(request);
            Iterator var3 = this.mapping.getPaths().iterator();

            String path;
            do {
                if (!var3.hasNext()) {
                    return true;
                }

                path = (String)var3.next();
            } while(!requestPath.startsWith(this.mapping.getPath(path)));

            return false;
        }

        private String getRequestPath(HttpServletRequest request) {
            String url = request.getServletPath();
            if (request.getPathInfo() != null) {
                url = url + request.getPathInfo();
            }

            return url;
        }
    }
}
