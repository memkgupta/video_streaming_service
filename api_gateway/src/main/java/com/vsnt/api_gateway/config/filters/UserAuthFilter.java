package com.vsnt.api_gateway.config.filters;

import com.vsnt.api_gateway.config.UserRouteValidator;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;
@Component
public class UserAuthFilter extends AbstractGatewayFilterFactory<UserAuthFilter.Config> {

    private final RestTemplate restTemplate;
private final UserRouteValidator routeValidator;
    public UserAuthFilter(RestTemplate restTemplate, UserRouteValidator routeValidator) {
        super(Config.class);
        this.restTemplate = restTemplate;
        this.routeValidator = routeValidator;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {
            ServerHttpRequest request = null;
            if(routeValidator.isSecured.test(exchange.getRequest())) {
                String authHeader = Objects.requireNonNull(exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }
                else{
                    throw new RuntimeException("missing authorization header");
                }
                try {
//                    REST call to AUTH service
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Authorization", "Bearer "+authHeader);
                    HttpEntity<String> entity = new HttpEntity<>(headers);
                    ResponseEntity<Map> response = restTemplate.exchange( "http://localhost:8080/auth/authenticate",
                            HttpMethod.GET,
                            entity,
                            Map.class);

                    assert response.getBody() != null;
                    String userId = (String) response.getBody().get("id");
                    request= exchange.getRequest().mutate().header("X-USER-ID", userId).build();
                    exchange = exchange.mutate().request(request).build();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("invalid access...!");
                    throw new RuntimeException("un-authorized access to application");
                }
            }
            return chain.filter(exchange);
        }));
    }

    public static class Config{}
}
