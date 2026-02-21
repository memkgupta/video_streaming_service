package com.vsnt.asset_onboarding.services;


import java.nio.file.Paths;
import java.security.PrivateKey;
import java.time.Instant;
import java.util.Date;

import com.vsnt.asset_onboarding.config.Secrets;
import com.vsnt.asset_onboarding.dtos.security.SignedCookie;
import com.vsnt.asset_onboarding.utils.CookiesService;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.cookie.CookiesForCannedPolicy;
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;
import com.vsnt.asset_onboarding.utils.KeyLoader;

@Service
public class CloudFrontCookieService implements CookiesService {

    private final String keyPairId = Secrets.CLOUDFRONT_KEY_PAIR_ID;
    private final String privateKeyPath = Secrets.PRIVATE_KEY_PATH;
    private final String resourceURL = Secrets.CDN_RESOURCE_URL;
    private static final CloudFrontUtilities cloudFrontUtilities = CloudFrontUtilities.create();
    public SignedCookie generateCookies()  {



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
}