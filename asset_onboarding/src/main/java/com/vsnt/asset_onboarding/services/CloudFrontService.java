package com.vsnt.asset_onboarding.services;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.vsnt.asset_onboarding.CDNService;
import com.vsnt.asset_onboarding.SecuredCDNService;
import com.vsnt.asset_onboarding.config.CDNSecurityConfig;
import com.vsnt.asset_onboarding.dtos.security.SignedCookie;

import org.springframework.stereotype.Service;

@Service
public class CloudFrontService implements CDNService , SecuredCDNService {
    public CloudFrontService(CDNSecurityConfig cdnSecurityConfig) {
        this.cdnSecurityConfig = cdnSecurityConfig;
    }

    @Override
    public byte[] fetchSecure(String path) {
        String signedURL = generateSignedURL(path);
        return fetch(signedURL);
    }

    private final CDNSecurityConfig cdnSecurityConfig;
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
    public String generateSignedURLWildcard(String path)
    {
    return cdnSecurityConfig.generateSignedURL(path,true);
    }
    public SignedCookie generateCookies()  {
    return cdnSecurityConfig.generateSignedCookie();
    }
    private String generateSignedURL(String path)
    {
        return cdnSecurityConfig.generateSignedURL(path);
    }
}