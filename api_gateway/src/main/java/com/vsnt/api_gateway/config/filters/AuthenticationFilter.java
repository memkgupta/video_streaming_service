package com.vsnt.api_gateway.config.filters;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import com.vsnt.api_gateway.config.RouteValidator;

import java.util.Map;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    public AuthenticationFilter() {
        super(AuthenticationFilter.Config.class);
    }

    @Autowired
    private RouteValidator routeValidator;
    @Autowired
    RestTemplate restTemplate;
    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {


            try{
                ServerHttpRequest request = null;
                if(routeValidator.isSecured.test(exchange.getRequest())) {

                    if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                        throw new RuntimeException("missing authorization header");
                    }
                    String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        authHeader = authHeader.substring(7);
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

                        String userId = (String) response.getBody().get("userId");
                        request= exchange.getRequest().mutate().header("X-USER-ID", userId).build();
                        exchange = exchange.mutate().request(request).build();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("invalid access...!");
                        throw new RuntimeException("un-authorized access to application");
                    }
                }
                return chain.filter(exchange);
            }
            catch(Exception e){
                e.printStackTrace();
                throw new RuntimeException("Something went wrong");
            }

        }));
    }

    public static class Config {}
}
