package com.vsnt.asset_onboarding.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    private final AuthenticateRequest authenticateRequest;
    private final AuthFilter authFilter;
    public FilterConfig(AuthenticateRequest authenticateRequest) {
        this.authenticateRequest = authenticateRequest;
        this.authFilter = new AuthFilter(authenticateRequest);
    }

//    @Bean
//    public FilterRegistrationBean<AuthFilter> authFilterf() {
//        FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
//
//        registrationBean.setFilter(authFilter);
//        registrationBean.addUrlPatterns("/api/*"); // apply to specific routes
//        registrationBean.setOrder(0); // priority if multiple filters
//
//        return registrationBean;
//    }
}
