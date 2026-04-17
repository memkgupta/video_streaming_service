package com.vsnt.asset_onboarding.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@ConditionalOnExpression(
        value =
                "'${app.cdn.security.enabled:false}' == 'false'"
)
@Component
public class DefaultCDNSecurityConfig extends CDNSecurityConfig{
    private final String resourceURL = Secrets.CDN_RESOURCE_URL;

    @Override
    public String generateSignedURL(String path, boolean wildcard) {
        if(wildcard)
        {
            throw new IllegalStateException("Can't generate SignedURL with wildcard");

        }
        else return generateSignedURL(path);
    }

    @Override
    public String generateSignedURL(String path) {
        return resourceURL + path;
    }
}
