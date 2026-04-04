package com.vsnt.api_gateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/eureka",
            "/swagger-ui",
            "/api/user/v3/api-docs",

            "/api/asset_onboarding/v3/api-docs",
            "/api/asset_onboarding/v1/live",
            "/api/asset_onboarding/v1/file",

            "/api/user/v1/auth/register",
            "/api/user/v1/auth/login",
            "/api/user/v1/token/refresh-token"
//            "/api/asset_onboarding/v1/watch"
    );
/*all the secured routes need a api key in there request */
    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}