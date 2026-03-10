package com.vsnt.asset_onboarding.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;
import software.amazon.awssdk.services.cloudfront.model.CustomSignerRequest;
import software.amazon.awssdk.services.cloudfront.url.SignedUrl;

import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
@ConditionalOnExpression(
        value =
                "'${app.cdn.security.enabled:false}' == 'true'"
)
@Component
public class ProdCDNSecurityConfig extends CDNSecurityConfig {


    @Override
    public String generateSignedURL(String path, boolean wildcard) {
        CustomSignerRequest request = null;
        if(wildcard)
        {
            Instant expirationDate = Instant.now().plus(1, ChronoUnit.DAYS);

            try {
                request = CustomSignerRequest.builder()
                        .keyPairId(keyPairId)
                        .privateKey(Paths.get(privateKeyPath))
                        .resourceUrl(Secrets.CDN_RESOURCE_URL+path+"/*")
                        .expirationDate(expirationDate)
                        .build();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return cloudFrontUtilities.getSignedUrlWithCustomPolicy(request).url();

        }
        else {
            return generateSignedURL(path);
        }

    }

    @Override
    public String generateSignedURL(String path) {
        Instant expirationDate = Instant.now().plus(1, ChronoUnit.HOURS);
        CannedSignerRequest request = null;
        try {
            request = CannedSignerRequest.builder()
                    .keyPairId(this.keyPairId)
                    .privateKey(Paths.get(privateKeyPath))
                    .resourceUrl(resourceURL+path)
                    .expirationDate(expirationDate)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SignedUrl signedUrl = cloudFrontUtilities.getSignedUrlWithCannedPolicy(request);
        return signedUrl.url();
    }
}
