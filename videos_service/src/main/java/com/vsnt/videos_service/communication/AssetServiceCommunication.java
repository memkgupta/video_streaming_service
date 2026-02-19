package com.vsnt.videos_service.communication;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Service
public class AssetServiceCommunication {

    private final RestTemplate restTemplate;

    @Value("${asset.service.base-url}")
    private String assetServiceBaseUrl;

    public AssetServiceCommunication(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String createAsset(String url) {

        String endpoint =
                assetServiceBaseUrl + "/assets";



        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> httpEntity =
                new HttpEntity<>(url, headers);

        ResponseEntity<HashMap> response =
                restTemplate.exchange(
                        endpoint,
                        HttpMethod.POST,
                        httpEntity,
                      HashMap.class
                );

        if (response.getStatusCode().is2xxSuccessful()
                && response.getBody() != null) {
            System.out.println(response.getBody());
            return "abcId123";
        }

        throw new RuntimeException("Failed to create asset");
    }
}
