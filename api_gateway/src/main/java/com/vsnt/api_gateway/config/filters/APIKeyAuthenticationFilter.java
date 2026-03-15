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
public class APIKeyAuthenticationFilter extends AbstractGatewayFilterFactory<APIKeyAuthenticationFilter.Config> {

    public APIKeyAuthenticationFilter() {
        super(APIKeyAuthenticationFilter.Config.class);
    }

    @Autowired
    private RouteValidator routeValidator;
    @Autowired
    RestTemplate restTemplate;
    private final String AUTHENTICATOR_URL = "http://localhost:8080/api/v1/authorise/validate-key";
    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {

                ServerHttpRequest request = null;
                if(routeValidator.isSecured.test(exchange.getRequest())) {
                    String apiKey = exchange.getRequest()
                            .getHeaders()
                            .getFirst("X-ACCESS-KEY");

                    String apiSecret = exchange.getRequest()
                            .getHeaders()
                            .getFirst("X-ACCESS-SECRET");
                   if(apiSecret == null || apiSecret.isEmpty() || apiKey == null || apiKey.isEmpty())
                   {
                       throw new RuntimeException("Access key or Access secret not found");
                   }

                    try {
//                    REST call to AUTH service
                        System.out.println(apiKey);
                        System.out.println(apiSecret);
                       HttpHeaders headers = new HttpHeaders();
                       headers.set("X-ACCESS-KEY", apiKey);
                       headers.set("X-ACCESS-SECRET", apiSecret);
                        HttpEntity<String> entity = new HttpEntity<>(headers);
                        ResponseEntity<Map> response = restTemplate.exchange( AUTHENTICATOR_URL,
                                HttpMethod.GET,
                                entity,
                                Map.class);

                       String orgId = response.getBody().get("organisationId").toString();
                        request= exchange.getRequest().mutate().header("X-ORG-ID", orgId).build();
                        exchange = exchange.mutate().request(request).build();
                    } catch (Exception e) {

                        System.out.println("invalid access...!");
                        throw new RuntimeException("un-authorized access to application");
                    }
                }
                return chain.filter(exchange);
        }));
    }

    public static class Config {}
}
