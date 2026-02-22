package com.vsnt.asset_onboarding.services;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import com.vsnt.asset_onboarding.CDNService;
import com.vsnt.asset_onboarding.KeyCDNService;
import com.vsnt.asset_onboarding.config.Secrets;
import com.vsnt.asset_onboarding.dtos.security.SignedCookie;
import com.vsnt.asset_onboarding.utils.CookiesService;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.cookie.CookiesForCannedPolicy;
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;
import software.amazon.awssdk.services.cloudfront.url.SignedUrl;

@Service
public class CloudFrontService implements CookiesService , CDNService , KeyCDNService {
    @Override
    public byte[] fetchSecure(String path) {
        String signedURL = generateSignedURL();
        return fetch(signedURL);
    }

    private final String keyPairId = Secrets.CLOUDFRONT_KEY_PAIR_ID;
    private final String privateKeyPath = Secrets.PRIVATE_KEY_PATH;
    private final String resourceURL = Secrets.CDN_RESOURCE_URL;
    private static final CloudFrontUtilities cloudFrontUtilities = CloudFrontUtilities.create();
    private final HttpClient httpClient =
            HttpClient.newHttpClient();

    public byte[] fetch(String url) {

        try {

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .GET()
                            .build();

            HttpResponse<byte[]> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofByteArray()
                    );

            return response.body();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
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
    private String generateSignedURL()
    {
        Instant expirationDate = Instant.now().plus(1, ChronoUnit.HOURS);
        CannedSignerRequest request = null;
        try {
            request = CannedSignerRequest.builder()
                    .keyPairId(keyPairId)
                    .privateKey(Paths.get(privateKeyPath))
                    .resourceUrl(resourceURL)
                    .expirationDate(expirationDate)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SignedUrl signedUrl = cloudFrontUtilities.getSignedUrlWithCannedPolicy(request);
        return signedUrl.url();
    }
}