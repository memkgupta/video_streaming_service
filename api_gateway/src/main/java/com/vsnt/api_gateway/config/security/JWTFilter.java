package com.vsnt.api_gateway.config.security;

import com.vsnt.api_gateway.config.RouteValidator;
import com.vsnt.api_gateway.utils.JWTService;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(JWTFilter.class);

    private final JWTService jwtService;
    private final RouteValidator routeValidator;

    public JWTFilter(JWTService jwtService, RouteValidator routeValidator) {
        this.jwtService = jwtService;
        this.routeValidator = routeValidator;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String path = exchange.getRequest().getPath().toString();

        log.info("========== JWT FILTER START ==========");
        log.info("Path: {}", path);
        log.info("Method: {}", exchange.getRequest().getMethod());

        if (!routeValidator.isSecured.test(exchange.getRequest())) {
            log.info("Route is NOT secured. Forwarding request.");
            return chain.filter(exchange);
        }

        log.info("Route IS secured");

        return ReactiveSecurityContextHolder.getContext()
                .doOnSubscribe(s ->
                        log.info("Checking SecurityContext"))
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {

                    log.info("Authentication found in SecurityContext");

                    if (authentication != null) {
                        log.info(
                                "Principal={}, Authenticated={}",
                                authentication.getPrincipal(),
                                authentication.isAuthenticated()
                        );
                    }

                    if (authentication != null &&
                            authentication.isAuthenticated()) {

                        log.info("Request already authenticated. Forwarding.");

                        return chain.filter(exchange);
                    }

                    String authHeader = exchange.getRequest()
                            .getHeaders()
                            .getFirst("Authorization");

                    log.info("Authorization header present: {}",
                            authHeader != null);

                    if (authHeader != null) {
                        return authenticateWithJwt(exchange, chain);
                    }

                    return processAccessToken(exchange, chain);
                })
                .switchIfEmpty(Mono.defer(() -> {

                    log.warn("SecurityContext is EMPTY");

                    String authHeader = exchange.getRequest()
                            .getHeaders()
                            .getFirst("Authorization");

                    if (authHeader != null) {
                        log.info("Attempting JWT authentication");
                        return authenticateWithJwt(exchange, chain);
                    }

                    log.info("Attempting Access Token authentication");
                    return processAccessToken(exchange, chain);
                }))
                .doOnSuccess(v ->
                        log.info("JWTFilter completed successfully"))
                .doOnError(e ->
                        log.error("JWTFilter failed", e));
    }

    private Mono<Void> processAccessToken(
            ServerWebExchange exchange,
            WebFilterChain chain) {

        log.info("Processing X-ACCESS-TOKEN");

        String token = exchange.getRequest()
                .getHeaders()
                .getFirst("X-ACCESS-TOKEN");

        if (token == null || token.isEmpty()) {

            log.warn("X-ACCESS-TOKEN missing");
            log.info("Forwarding request without access token");

            return chain.filter(exchange);
        }

        log.info("X-ACCESS-TOKEN found");

        try {

            if (jwtService.isTokenExpired(token)) {

                log.warn("X-ACCESS-TOKEN expired");

                exchange.getResponse()
                        .setStatusCode(HttpStatus.UNAUTHORIZED);

                return exchange.getResponse().setComplete();
            }

            String assetId = jwtService.extractClaims(
                    token,
                    claims -> claims.get("assetId", String.class)
            );

            String userId = jwtService.extractClaims(
                    token,
                    Claims::getSubject
            );

            log.info("Access Token validated");
            log.info("AssetId={}", assetId);
            log.info("UserId={}", userId);

            ServerHttpRequest mutatedRequest =
                    exchange.getRequest()
                            .mutate()
                            .header("X-ASSET-ID", assetId)
                            .header("X-USER-ID", userId)
                            .build();

            ServerWebExchange mutatedExchange =
                    exchange.mutate()
                            .request(mutatedRequest)
                            .build();

            Authentication auth =
                    new PreAuthenticatedAuthenticationToken(
                            assetId,
                            null,
                            new ArrayList<>()
                    );

            log.info("Forwarding request to downstream service");

            return chain.filter(mutatedExchange)
                    .contextWrite(
                            ReactiveSecurityContextHolder
                                    .withAuthentication(auth)
                    );

        } catch (Exception e) {

            log.error("Access token validation failed", e);

            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }
    }

    private Mono<Void> authenticateWithJwt(
            ServerWebExchange exchange,
            WebFilterChain chain) {

        log.info("Processing Authorization JWT");

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            log.warn("Invalid Authorization header");

            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        log.info("JWT token extracted");

        return jwtService.validate(token)
                .doOnSubscribe(s ->
                        log.info("Validating JWT"))
                .doOnNext(user ->
                        log.info(
                                "JWT validated successfully for user={}",
                                user.getUsername()))
                .flatMap(userDetails -> {

                    Authentication auth =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails.getUsername(),
                                    null,
                                    userDetails.getAuthorities()
                            );

                    ServerHttpRequest mutatedRequest =
                            exchange.getRequest()
                                    .mutate()
                                    .header(
                                            "X-USER-ID",
                                            userDetails.getUsername())
                                    .build();

                    ServerWebExchange mutatedExchange =
                            exchange.mutate()
                                    .request(mutatedRequest)
                                    .build();

                    log.info(
                            "Authentication successful. Forwarding request.");

                    return chain.filter(mutatedExchange)
                            .contextWrite(
                                    ReactiveSecurityContextHolder
                                            .withAuthentication(auth)
                            );
                })
                .onErrorResume(e -> {

                    log.error("JWT validation failed", e);

                    exchange.getResponse()
                            .setStatusCode(HttpStatus.UNAUTHORIZED);

                    return exchange.getResponse().setComplete();
                });
    }
}