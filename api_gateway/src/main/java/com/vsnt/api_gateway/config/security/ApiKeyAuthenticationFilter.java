package com.vsnt.api_gateway.config.security;

import com.vsnt.api_gateway.config.RouteValidator;
import com.vsnt.api_gateway.config.dtos.ApiKeyValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ApiKeyAuthenticationFilter implements WebFilter {

    private static final Logger log =
            LoggerFactory.getLogger(ApiKeyAuthenticationFilter.class);

    private final WebClient webClient;

    private static final String ACCESS_KEY_HEADER = "X-ACCESS-KEY";
    private static final String SECRET_KEY_HEADER = "X-ACCESS-SECRET";

    private final RouteValidator routeValidator;

    public ApiKeyAuthenticationFilter(
            WebClient.Builder builder,
            RouteValidator routeValidator
    ) {
        this.webClient = builder.build();
        this.routeValidator = routeValidator;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             WebFilterChain chain) {

        String path = exchange.getRequest().getPath().toString();

        log.info("=================================================");
        log.info("API KEY FILTER START");
        log.info("Path: {}", path);
        log.info("Method: {}", exchange.getRequest().getMethod());

        if (!routeValidator.isSecured.test(exchange.getRequest())) {
            log.info("Route is NOT secured. Forwarding.");
            return chain.filter(exchange);
        }

        log.info("Route IS secured");

        return ReactiveSecurityContextHolder.getContext()
                .doOnSubscribe(s ->
                        log.info("Looking for SecurityContext"))
                .doOnNext(ctx ->
                        log.info("SecurityContext found"))
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {

                    log.info("Inside authentication flatMap");

                    if (authentication != null) {
                        log.info(
                                "Authentication found. Principal={}, Authenticated={}",
                                authentication.getPrincipal(),
                                authentication.isAuthenticated()
                        );
                    } else {
                        log.info("Authentication is null");
                    }

                    if (authentication != null &&
                            authentication.isAuthenticated()) {

                        log.info("Already authenticated. Forwarding request.");

                        return chain.filter(exchange);
                    }

                    return processAPIKeyAuthentication(exchange, chain);
                })
                .switchIfEmpty(Mono.defer(()->processAPIKeyAuthentication(exchange, chain)))
                .doOnSuccess(v ->
                        log.info("API KEY FILTER COMPLETED"))
                .doOnError(e ->
                        log.error("API KEY FILTER FAILED", e));
    }

    private Mono<Void> processAPIKeyAuthentication(ServerWebExchange exchange, WebFilterChain chain) {
        String accessKey = exchange.getRequest()
                .getHeaders()
                .getFirst(ACCESS_KEY_HEADER);

        String secretKey = exchange.getRequest()
                .getHeaders()
                .getFirst(SECRET_KEY_HEADER);

        log.info("Access Key Present: {}", accessKey != null);
        log.info("Secret Key Present: {}", secretKey != null);

        if (accessKey != null
                && !accessKey.isEmpty()
                && secretKey != null
                && !secretKey.isEmpty()) {

            log.info("Calling validateAPIKey service");

            return validateAPIKey(accessKey, secretKey)
                    .doOnSubscribe(s ->
                            log.info("Validation request started"))
                    .doOnNext(res ->
                            log.info("Validation response received: {}", res))
                    .flatMap(res -> {

                        if (res == null || !res.isValid()) {

                            log.warn("API key validation failed");

                            exchange.getResponse()
                                    .setStatusCode(HttpStatus.UNAUTHORIZED);

                            return exchange.getResponse()
                                    .setComplete();
                        }

                        log.info("API key validation successful");
                        log.info("OrgId={}", res.getOrgId());

                        ServerHttpRequest mutatedRequest =
                                exchange.getRequest()
                                        .mutate()
                                        .header("X-Org-Id",
                                                res.getOrgId())
                                        .build();

                        ServerWebExchange mutatedExchange =
                                exchange.mutate()
                                        .request(mutatedRequest)
                                        .build();

                        List<GrantedAuthority> grantedAuthorities =
                                res.getRoles()
                                        .stream()
                                        .map(SimpleGrantedAuthority::new)
                                        .collect(Collectors.toList());

                        Authentication auth =
                                new PreAuthenticatedAuthenticationToken(
                                        res.getOrgId(),
                                        null,
                                        grantedAuthorities
                                );

                        log.info("Forwarding request to next filter");

                        return chain.filter(mutatedExchange)
                                .contextWrite(
                                        ReactiveSecurityContextHolder
                                                .withAuthentication(auth)
                                );
                    })
                    .onErrorResume(err -> {

                        log.error("API key validation error", err);

                        exchange.getResponse()
                                .setStatusCode(
                                        HttpStatus.INTERNAL_SERVER_ERROR);

                        return exchange.getResponse()
                                .setComplete();
                    });
        }

        log.info("No API key headers present. Forwarding request");

        return chain.filter(exchange);
    }

    private Mono<ApiKeyValidationResponse> validateAPIKey(
            String accessKey,
            String secretKey
    ) {

        log.info("Calling user service for API key validation");

        return webClient.get()
                .uri("lb://user/v1/authorise/validate-key")
                .header("X-ACCESS-KEY", accessKey)
                .header("X-ACCESS-SECRET", secretKey)
                .retrieve()
                .bodyToMono(ApiKeyValidationResponse.class)
                .doOnNext(res ->
                        log.info("Validation service response: {}", res))
                .doOnError(err ->
                        log.error("Validation service call failed", err));
    }
}