package com.vsnt.api_gateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/api/user/auth/register",
            "/api/user/auth/login",
            "/api/user/token/refresh-token",
            "/eureka",
            "/api/channel/v3/api-docs",
            "/api/user/v3/api-docs",
            "/api/transcoder/v3/api-docs",
            "/api/asset_onboarding/v3/api-docs",
            "/api/video/v3/api-docs",
            "/api/aggregate/v3/api-docs"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}