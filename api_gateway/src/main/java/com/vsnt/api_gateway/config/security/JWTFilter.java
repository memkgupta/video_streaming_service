package com.vsnt.api_gateway.config.security;

import com.vsnt.api_gateway.config.RouteValidator;
import com.vsnt.api_gateway.utils.JWTService;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Component
public class JWTFilter implements WebFilter {
    private final JWTService jwtService;
    private final RouteValidator routeValidator;

    public JWTFilter(JWTService jwtService, RouteValidator routeValidator) {
        this.jwtService = jwtService;
        this.routeValidator = routeValidator;
    }
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!routeValidator.isSecured.test(exchange.getRequest())) {
            return chain.filter(exchange);
        }

        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    if (authentication != null && authentication.isAuthenticated()) {
                        return chain.filter(exchange);
                    }
                    String authHeader = exchange.getRequest()
                            .getHeaders()
                            .getFirst("Authorization");

                    return authHeader!=null ? authenticateWithJwt(exchange, chain):processAccessToken(exchange, chain);
                })
                .switchIfEmpty(Mono.defer(() ->{
                    String authHeader = exchange.getRequest()
                            .getHeaders()
                            .getFirst("Authorization");

                    return authHeader!=null ? authenticateWithJwt(exchange, chain):processAccessToken(exchange, chain);
                }));
    }
    private Mono<Void> processAccessToken(ServerWebExchange exchange, WebFilterChain chain) {
        String raw = exchange.getRequest().getHeaders().getFirst("X-ACCESS-TOKEN");

        // No token — pass through, let other filters or security handle it
        if (raw == null || raw.isEmpty()) {
            return chain.filter(exchange);
        }



        String token = raw;

        if (jwtService.isTokenExpired(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String assetId = jwtService.extractClaims(token, claims -> claims.get("assetId", String.class));
        String userId = jwtService.extractClaims(token, Claims::getSubject);

        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header("X-ASSET-ID", assetId)
                .header("X-USER-ID", userId)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        Authentication auth = new PreAuthenticatedAuthenticationToken(
                assetId, null, new ArrayList<>()
        );

        return chain.filter(mutatedExchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
    }
    private Mono<Void> authenticateWithJwt(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        System.out.println("Auth header : " + authHeader);
        String token = authHeader.substring(7);
        System.out.println("token: " + token);
        return jwtService.validate(token)
                .flatMap(userDetails -> {
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            userDetails.getUsername(), null, userDetails.getAuthorities()
                    );
                    System.out.println("auth: " + auth);
                    ServerHttpRequest mutatedRequest = exchange.getRequest()
                            .mutate()
                            .header("X-USER-ID", userDetails.getUsername())
                            .build();

                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(mutatedRequest)
                            .build();

                    return chain.filter(mutatedExchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                })
                .onErrorResume(e -> {
                    e.printStackTrace();
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });
    }
}
