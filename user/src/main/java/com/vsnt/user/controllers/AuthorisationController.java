package com.vsnt.user.controllers;

import com.vsnt.user.entities.APIKey;
import com.vsnt.user.services.APIKeyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/authorise")
public class AuthorisationController {

    private final APIKeyService apiKeyService;
    public AuthorisationController( APIKeyService apiKeyService) {

        this.apiKeyService = apiKeyService;
    }


    @GetMapping("/validate-key")
    public ResponseEntity<?> apiKeyAuth(@RequestHeader("X-ACCESS-KEY") UUID accessKey ,@RequestHeader("X-ACCESS-SECRET") String secretKey)
    {
        /*this endpoint will be called by the api gateway for authorising request made by the org's product through sdk*/
        APIKey apiKey = apiKeyService.findByAccessKeyAndSecret(
              accessKey, secretKey
        );
        if(apiKey == null){
            throw new RuntimeException("Invalid Access Key Or Secret Key");
        }
        return ResponseEntity.ok(Map.of("organisationId",apiKey.getOrganisation().getId().toString()));
    }

}
