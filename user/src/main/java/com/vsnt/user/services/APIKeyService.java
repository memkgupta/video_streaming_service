package com.vsnt.user.services;

import com.vsnt.user.entities.APIKey;
import com.vsnt.user.entities.Organisation;
import com.vsnt.user.repositories.APIKeyRepository;
import com.vsnt.user.utils.ApiKeyGenerator;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class APIKeyService {

    private final APIKeyRepository apiKeyRepository;
    public APIKeyService( APIKeyRepository apiKeyRepository) {

        this.apiKeyRepository = apiKeyRepository;
    }

    public APIKey generateAPIKey(Organisation org) {

        APIKey apiKey = new APIKey();
        apiKey.setValid(true);
        apiKey.setSecret(ApiKeyGenerator.generateApiKey(32));

        apiKey.setOrganisation(org);
        return apiKeyRepository.save(apiKey);
    }
    private APIKey findByAccessKey(UUID accessKey) {
        return apiKeyRepository.findByAccessKey(accessKey).orElse(null);
    }
    public APIKey findByAccessKeyAndSecret(UUID accessKey,String secret) {
        APIKey apiKey = apiKeyRepository.findByAccessKey(accessKey).orElse(null);
        if(apiKey == null){
            return null;
        }
        if(!secret.equals(apiKey.getSecret())){
           return null;
        }
        return apiKey;
    }

}
