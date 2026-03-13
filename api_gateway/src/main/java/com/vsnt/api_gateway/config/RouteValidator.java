package com.vsnt.api_gateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(

            "/eureka",
            "/api/channel/v3/api-docs",
            "/api/user/v3/api-docs",
            "/api/transcoder/v3/api-docs",
            "/api/asset_onboarding/v3/api-docs",
            "/api/video/v3/api-docs",
            "/api/aggregate/v3/api-docs",
            "/api/watch", // logic for authorisation at the service level
            "/api/live", // logic for authorisation at the service level
            "/api/assets",
            "/api/v1/key"// logic for authorisation at the service level
    );
/*all the secured routes need a api key in there request */
    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}