package com.vsnt.asset_onboarding.config;

import com.vsnt.asset_onboarding.dtos.AuthDTO;
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

    public AuthDTO isAuthenticated(String token)
    {
        AuthDTO isAuthenticated = null;
        Map<String,String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + token);
        try {
            String url = "http://localhost:8080/api/authenticate";
            isAuthenticated =  restTemplate.getForObject(url,AuthDTO.class,headers);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        assert isAuthenticated != null;
        return isAuthenticated;

    }
}
