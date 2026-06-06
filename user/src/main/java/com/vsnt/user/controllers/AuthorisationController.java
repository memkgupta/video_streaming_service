package com.vsnt.user.controllers;

import com.vsnt.user.dtos.responses.WebhookResponse;
import com.vsnt.user.entities.APIKey;
import com.vsnt.user.entities.Webhook;
import com.vsnt.user.services.APIKeyService;
import com.vsnt.user.services.WebhookService;
import com.vsnt.user.utils.encryption.WebhookSecretUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/authorise")
public class AuthorisationController {

    private final APIKeyService apiKeyService;
    private final WebhookService webhookService;
    private final WebhookSecretUtil webhookSecretUtil;
    public AuthorisationController(APIKeyService apiKeyService, WebhookService webhookService, WebhookSecretUtil webhookSecretUtil) {

        this.apiKeyService = apiKeyService;
        this.webhookService = webhookService;
        this.webhookSecretUtil = webhookSecretUtil;
    }


    @GetMapping("/validate-key")
    public ResponseEntity<?> apiKeyAuth(@RequestHeader("X-ACCESS-KEY") UUID accessKey ,@RequestHeader("X-ACCESS-SECRET") String secretKey)
    {
        System.out.println("apiKeyAuth ");

        /*this endpoint will be called by the api gateway for authorising request made by the org's product through sdk*/
        APIKey apiKey = apiKeyService.findByAccessKeyAndSecret(
              accessKey, secretKey
        );
        if(apiKey == null){
            throw new RuntimeException("Invalid Access Key Or Secret Key");
        }
        return ResponseEntity.ok(Map.of("orgId",apiKey.getOrganisation().getId().toString(),"roles",List.of(),"valid",true));
    }
    @GetMapping("/webhook-subscriptions")
            public ResponseEntity<List<WebhookResponse>> allWebHookSubscriptions(@RequestParam("orgId") String orgId , @RequestParam("eventType") String eventType)
              {
                  System.out.println(orgId);
                  System.out.println(eventType);
                  List<Webhook> webhooks = webhookService.getActiveSubscriptions(orgId, eventType);
                  return ResponseEntity.ok(webhooks.stream().map(w-> {
                              try {
                                  return WebhookResponse
                                          .builder()
                                          .id(w.getId())
                                          .secret(webhookSecretUtil.decrypt(w.getIv(), w.getEncrypted_secret()))
                                          .callbackUrl(w.getCallbackUrl())
                                          .build();
                              } catch (IllegalBlockSizeException | InvalidAlgorithmParameterException |
                                       InvalidKeyException | BadPaddingException | NoSuchPaddingException |
                                       NoSuchAlgorithmException e) {
                                  throw new RuntimeException(e);
                              }
                          }
                  ).toList());
              }
}
