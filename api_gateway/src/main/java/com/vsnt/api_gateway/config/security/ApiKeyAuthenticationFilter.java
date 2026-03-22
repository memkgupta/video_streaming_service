package com.vsnt.api_gateway.config.security;

import com.vsnt.api_gateway.config.RouteValidator;
import com.vsnt.api_gateway.config.dtos.ApiKeyValidationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
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
    private final WebClient webClient;
    private static final String ACCESS_KEY_HEADER = "X-ACCESS-KEY";
    private static final String SECRET_KEY_HEADER = "X-SECRET-KEY";
    private final RouteValidator routeValidator;

    public ApiKeyAuthenticationFilter(WebClient.Builder builder, RouteValidator routeValidator) {
        this.webClient = builder.build();
        this.routeValidator = routeValidator;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if(!routeValidator.isSecured.test(exchange.getRequest())) {
            return chain.filter(exchange);
        }
        String access_key =  exchange.getRequest().getHeaders().getFirst(ACCESS_KEY_HEADER);
        String secret_key =  exchange.getRequest().getHeaders().getFirst(SECRET_KEY_HEADER);
        if(access_key!=null && !access_key.isEmpty() && secret_key!=null && !secret_key.isEmpty()){
            System.out.println(access_key);
            System.out.println(secret_key);
          return validateAPIKey(access_key,secret_key)
                   .flatMap(res->
                   {
                       if(res==null || !res.isValid())
                       {
                           exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                           return exchange.getResponse().setComplete();
                       }
                       ServerHttpRequest mutatedRequest = exchange.getRequest()
                               .mutate()
                               .header("X-Org-Id", res.getOrgId())
                               .build();

                       ServerWebExchange mutatedExchange = exchange.mutate()
                               .request(mutatedRequest)
                               .build();
                       List<GrantedAuthority> grantedAuthorities =res.getRoles().stream().map(SimpleGrantedAuthority::new)
                               .collect(Collectors.toList());
                       Authentication auth = new PreAuthenticatedAuthenticationToken(res.getOrgId(),null,grantedAuthorities);
                       return chain.filter(mutatedExchange)
                               .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));

                   })
                  .onErrorResume(err->{
                      err.printStackTrace();
                      exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                      return exchange.getResponse().setComplete();
                  })
                  ;
        }
       return chain.filter(exchange);
    }
    private Mono<ApiKeyValidationResponse> validateAPIKey(String accessKey, String secretKey){
        List<GrantedAuthority> authorities = new ArrayList<>();
  return webClient.get()

                .uri("lb://user/v1/authorise/validate-key")
                .header("X-ACCESS-KEY", accessKey)
                .header("X-ACCESS-SECRET", secretKey)
                .retrieve()
                .bodyToMono(ApiKeyValidationResponse.class);

    }
}
