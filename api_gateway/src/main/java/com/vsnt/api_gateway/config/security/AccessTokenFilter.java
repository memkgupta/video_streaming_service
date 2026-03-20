package com.vsnt.api_gateway.config.security;

import com.vsnt.api_gateway.config.RouteValidator;
import com.vsnt.api_gateway.utils.JWTService;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
public class AccessTokenFilter implements WebFilter {
    private final JWTService jwtService;
    private final RouteValidator routeValidator;

    public AccessTokenFilter(JWTService jwtService, RouteValidator routeValidator) {
        this.jwtService = jwtService;
        this.routeValidator = routeValidator;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if(!routeValidator.isSecured.test(exchange.getRequest())) {
            return chain.filter(exchange);
        }
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .defaultIfEmpty(null)
                .flatMap(authentication -> {
                    if(authentication!=null && authentication.isAuthenticated())
                    {
                        return chain.filter(exchange);
                    }
                    String token = exchange.getRequest().getHeaders().getFirst("X-ACCESS-TOKEN");
                    if(token == null || token.isEmpty())
                    {
                        return chain.filter(exchange);
                    }
                    if(!token.startsWith("Bearer "))
                    {
                        exchange.getResponse().setStatusCode(HttpStatus.NON_AUTHORITATIVE_INFORMATION);
                        return exchange.getResponse().setComplete();
                    }
                    if(jwtService.isTokenExpired(token))
                    {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }
                    String assetId = jwtService.extractClaims(token,(claims)->{
                        return claims.get("assetId",String.class);
                    });
                    String userId = jwtService.extractClaims(token, Claims::getSubject);

                    ServerHttpRequest mutatedRequest = exchange.getRequest()
                            .mutate()
                            .header("X-ASSET-ID",assetId)
                            .header("X-USER-ID",userId)
                            .build();
                    ServerWebExchange mutatedExchange = exchange
                            .mutate()
                            .request(mutatedRequest).build();
                    List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
                    Authentication auth = new PreAuthenticatedAuthenticationToken(assetId,null,grantedAuthorities);
                    return chain.filter(mutatedExchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));

                });
    }
}
