package com.vsnt.asset_onboarding.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service

public class AuthenticateRequest {

    private final RestTemplate restTemplate ;

    public AuthenticateRequest(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean isAuthenticated(String token)
    {
        Boolean isAuthenticated = false;
        Map<String,String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + token);
        try {
            String url = "http://localhost:8080/api/authenticate";
            isAuthenticated =  restTemplate.getForObject(url,Boolean.class,headers);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        assert isAuthenticated != null;
        return isAuthenticated;

    }
}
