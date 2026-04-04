package com.vsnt.api_gateway.config.security;

import com.vsnt.api_gateway.config.RouteValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.regex.MatchResult;

@Configuration
@EnableWebFluxSecurity
public class ApplicationSecurityConfig {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ApiKeyAuthenticationFilter apiKeyAuthenticationFilter , JWTFilter jwtFilter , AccessTokenFilter accessTokenFilter, RouteValidator routeValidator) {
//        return http
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .authorizeExchange(exchange -> exchange
//                        .pathMatchers("/api/public/**", "/api/user/auth/**").permitAll()
//                        .anyExchange().authenticated()
//                )
//                .addFilterAt(apiKeyAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
//                .addFilterAfter(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
//                .addFilterAfter(accessTokenFilter,SecurityWebFiltersOrder.AUTHENTICATION)
//                .build();
        return http

                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> {
                    exchange.matchers(s->{
                        System.out.println("Checking security for "+s.getRequest().getPath() + s.getRequest().getMethod());
                        if(!routeValidator.isSecured.test(s.getRequest()) || s.getRequest().getMethod().equals(HttpMethod.OPTIONS))
                        {
                            System.out.println("Pased security");
                            return ServerWebExchangeMatcher.MatchResult.match();

                        }
                        return ServerWebExchangeMatcher.MatchResult.notMatch();
                    }).permitAll().anyExchange().authenticated();
                }    )
                .addFilterAt(apiKeyAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAfter(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)

                .build();
    }
}
