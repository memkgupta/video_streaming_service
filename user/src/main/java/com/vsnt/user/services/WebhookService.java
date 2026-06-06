package com.vsnt.user.services;

import com.vsnt.user.entities.Webhook;
import com.vsnt.user.repositories.WebhookRepository;
import com.vsnt.user.dtos.requests.CreateWebhookRequest;
import com.vsnt.user.dtos.responses.WebhookResponse;
import com.vsnt.user.utils.encryption.EncryptedData;
import com.vsnt.user.utils.encryption.WebhookSecretUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class WebhookService {

    private final WebhookRepository repository;
    private final WebhookSecretUtil secretEncryptor;
    public List<Webhook> getActiveSubscriptions(String orgId, String eventType) {
        return repository.findByOrgIdAndEventType(orgId, eventType);
    }

    public WebhookResponse create(CreateWebhookRequest request) {
        try {
            String secret = secretEncryptor.generateSecret();
            EncryptedData encryptedData = secretEncryptor.encrypt(secret);
            Webhook webhook = new Webhook();
            webhook.setOrgId(request.getOrgId());
            webhook.setCreatedAt(Instant.now());
            webhook.setIv(encryptedData.getIv());
            webhook.setEncrypted_secret(encryptedData.getCiphertext());
            webhook.setCallbackUrl(request.getCallbackUrl());
            webhook.setEventType(request.getEventType());
            webhook = repository.save(webhook);
           return WebhookResponse.builder()
                    .id(webhook.getId())
                    .eventType(webhook.getEventType())
                    .secret(secretEncryptor.decrypt(webhook.getIv(), webhook.getEncrypted_secret()))
                    .build();
        } catch (Exception e) {
           throw new RuntimeException(e);
        }

    }
    public Webhook getSubscription(String subscriptionId) {
        return repository.findById(subscriptionId).orElse(null);
    }
    public WebhookResponse updateSubscription(CreateWebhookRequest request , Webhook webhook) {
        if(!Objects.equals(webhook.getEventType(), request.getEventType())){
            webhook.setEventType(request.getEventType());
        }
        if(!Objects.equals(webhook.getCallbackUrl(), request.getCallbackUrl())){
            webhook.setCallbackUrl(request.getCallbackUrl());
        }

        repository.save(webhook);
        return WebhookResponse.builder()
                .id(webhook.getId())
                .active(webhook.isActive())
                .callbackUrl(webhook.getCallbackUrl())
                .build();
    }
    public void deleteSubscription(String subscriptionId)
    {
        try {
            repository.deleteById(subscriptionId);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public Page<Webhook> getSubscriptions(Pageable pageable , Specification<Webhook> subscriptionSpecification)
    {
            return repository.findAll(subscriptionSpecification,pageable);
    }

}
