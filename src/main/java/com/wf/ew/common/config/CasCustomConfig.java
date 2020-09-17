package com.wf.ew.common.config;



import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.AssertionThreadLocalFilter;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.annotation.WebFilter;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Component
public class CasCustomConfig {
    @Value("${cas.server-url-prefix}")
    private   String CAS_PREFIX_URL;
    @Value("${cas.client-host-url}")
    private   String APP_URL;
    @Value("${cas.server-login-url}")
    private   String CAS_URL;
    @Value("${casClientLogoutUrl}")
    private   String CAS_LOGOUT_URL;


    private static boolean casEnabled = true;

    public CasCustomConfig() {

    }


    @Bean
    public FilterRegistrationBean singleSignOutFilter() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new SingleSignOutFilter());
        filterRegistration.setEnabled(casEnabled);
        filterRegistration.addUrlPatterns("/*");
        filterRegistration.addInitParameter("casServerUrlPrefix", CAS_PREFIX_URL);
        filterRegistration.setOrder(3);
        return filterRegistration;
    }

    /**
     * 该过滤器负责用户的认证工作
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean authenticationFilter() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new AuthenticationFilter());
        filterRegistration.setEnabled(casEnabled);
        filterRegistration.addUrlPatterns("/v1");
        filterRegistration.addInitParameter("casServerLoginUrl", CAS_URL);
        filterRegistration.addInitParameter("serverName", APP_URL);
        filterRegistration.addInitParameter("useSession", "true");
        filterRegistration.addInitParameter("redirectAfterValidation", "true");
        filterRegistration.setOrder(4);
        return filterRegistration;
    }

    /**
     * 该过滤器负责对Ticket的校验工作,使用CAS 3.0协议
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean cas30ProxyReceivingTicketValidationFilter() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new Cas20ProxyReceivingTicketValidationFilter());

        filterRegistration.setEnabled(casEnabled);
        filterRegistration.addUrlPatterns("/*");
        filterRegistration.addInitParameter("casServerUrlPrefix",CAS_PREFIX_URL);
        filterRegistration.addInitParameter("serverName", APP_URL);
        filterRegistration.setOrder(5);
        return filterRegistration;
    }

    @Bean
    public FilterRegistrationBean httpServletRequestWrapperFilter() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new HttpServletRequestWrapperFilter());
        filterRegistration.setEnabled(true);
        filterRegistration.addUrlPatterns("/*");
        filterRegistration.setOrder(6);
        return filterRegistration;
    }

    /**
     * 该过滤器使得可以通过org.jasig.cas.client.util.AssertionHolder来获取用户的登录名。
     * 比如AssertionHolder.getAssertion().getPrincipal().getName()。
     * 这个类把Assertion信息放在ThreadLocal变量中，这样应用程序不在web层也能够获取到当前登录信息
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean assertionThreadLocalFilter() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new AssertionThreadLocalFilter());
        filterRegistration.setEnabled(true);
        filterRegistration.addUrlPatterns("/*");
        filterRegistration.setOrder(7);
        return filterRegistration;
    }
}

