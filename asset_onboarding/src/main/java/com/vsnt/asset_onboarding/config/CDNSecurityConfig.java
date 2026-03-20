package com.vsnt.asset_onboarding.config;

import com.vsnt.asset_onboarding.dtos.security.SignedCookie;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.cookie.CookiesForCannedPolicy;
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;

import java.nio.file.Paths;
import java.util.Date;

public abstract class CDNSecurityConfig {
    protected final String keyPairId = Secrets.CLOUDFRONT_KEY_PAIR_ID;
    protected final String privateKeyPath = Secrets.PRIVATE_KEY_PATH;
    protected final String resourceURL = Secrets.CDN_RESOURCE_URL;
    protected static final CloudFrontUtilities cloudFrontUtilities = CloudFrontUtilities.create();
    public abstract String generateSignedURL(String path , boolean wildcard);
    public abstract   String generateSignedURL(String path);
    public SignedCookie generateSignedCookie() {
        Date expiration = new Date(System.currentTimeMillis() + 3600000);
        CannedSignerRequest request = null;
        try {
            request = CannedSignerRequest.builder()
                    .keyPairId(keyPairId)
                    .privateKey(Paths.get(privateKeyPath))
                    .resourceUrl(resourceURL)
                    .expirationDate(expiration.toInstant())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        CookiesForCannedPolicy cldCookie =  cloudFrontUtilities.getCookiesForCannedPolicy(request);
        return SignedCookie.builder()
                .expires(cldCookie.expiresHeaderValue())
                .keyPairId(cldCookie.keyPairIdHeaderValue())
                .signature(cldCookie.signatureHeaderValue())
                .build();
    }
//    public SignedCookie gen
}
