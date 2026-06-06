package com.vsnt.user.controllers;

import com.vsnt.common_lib.dtos.response.PageResponseDTO;
import com.vsnt.user.dtos.requests.CreateWebhookRequest;
import com.vsnt.user.dtos.responses.WebhookResponse;
import com.vsnt.user.entities.Organisation;
import com.vsnt.user.entities.Webhook;
import com.vsnt.user.exceptions.BadRequestException;
import com.vsnt.user.services.OrganisationService;
import com.vsnt.user.services.WebhookService;
import com.vsnt.user.utils.encryption.WebhookSecretUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/webhooks")
public class WebhookController {
    private final WebhookService webhookService;
    private final OrganisationService organisationService;
    private final WebhookSecretUtil webhookSecretUtil;
    public WebhookController(WebhookService webhookService, OrganisationService organisationService, WebhookSecretUtil webhookSecretUtil) {
        this.webhookService = webhookService;
        this.organisationService = organisationService;
        this.webhookSecretUtil = webhookSecretUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<WebhookResponse> register(@RequestBody CreateWebhookRequest request, @RequestHeader("X-USER-ID") String userId)
    {
            Organisation org = organisationService.findByAdmin(userId);
            if(org==null)
            {
                throw new BadRequestException("Organisation Not Found");
            }
            request.setOrgId(org.getId().toString());
            WebhookResponse subscription = webhookService.create(request);
            return ResponseEntity.ok(
                subscription
            );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id ,  @RequestHeader("X-USER-ID") String userId )
    {
            Organisation org = organisationService.findByAdmin(userId);
            if(org==null)
            {
                throw new BadRequestException("Organisation Not Found");
            }
            String orgId = org.getId().toString();
            Webhook webhook = webhookService.getSubscription(id);
            if(webhook == null || !webhook.getOrgId().equals(orgId))
            {
                return ResponseEntity.notFound().build();
            }
            webhookService.deleteSubscription(id);
            return ResponseEntity.noContent().build();

    }

    @PutMapping("/{id}")
    public ResponseEntity<WebhookResponse> update(@PathVariable String id, @RequestBody CreateWebhookRequest request , @RequestHeader("X-USER-ID") String userId )
    {
        Organisation org = organisationService.findByAdmin(userId);
        if(org==null)
        {
            throw new BadRequestException("Organisation Not Found");
        }
        String orgId = org.getId().toString();
            Webhook webhook = webhookService.getSubscription(id);
            if(webhook == null || !webhook.getOrgId().equals(orgId))
            {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(webhookService.updateSubscription(request, webhook));


    }
    @GetMapping
    public ResponseEntity<PageResponseDTO<WebhookResponse>> getWebhooks(@RequestParam(value = "page",defaultValue = "0") int page ,
                                                                             @RequestParam(value = "limit",defaultValue = "10") int limit,
                                                                             @RequestHeader("X-USER-ID") String userId
                                                                                  )
    {
        Organisation org = organisationService.findByAdmin(userId);
        if(org==null)
        {
            throw new BadRequestException("Organisation Not Found");
        }
        String orgId = org.getId().toString();
        Page<Webhook> subscriptions = webhookService.getSubscriptions(Pageable.ofSize(limit).withPage(page), Specification.where(
                ((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("orgId"), orgId))
        ));
        List<WebhookResponse> webhookResponseList = subscriptions.getContent().stream()
                .map((s)->
                        WebhookResponse.builder()
                                .callbackUrl(s.getCallbackUrl())
                                .id(s.getId())
                                .active(s.isActive())
                                .createdAt(s.getCreatedAt()).build()
                ).toList();
        return ResponseEntity.ok(
                PageResponseDTO.<WebhookResponse>builder()
                        .total(subscriptions.getTotalElements())
                        .hasPrevious(subscriptions.hasPrevious())
                        .hasNext(subscriptions.hasNext())
                        .data(webhookResponseList)
                        .page(subscriptions.getNumber())
                        .build()
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<WebhookResponse> getWebhook(@PathVariable String id  , @RequestHeader("X-USER-ID") String userId )
    {
        Organisation org = organisationService.findByAdmin(userId);
        if(org==null)
        {
            throw new BadRequestException("Organisation Not Found");
        }
        String orgId = org.getId().toString();
        Webhook webhook = webhookService.getSubscription(id);
        if(webhook == null || !webhook.getOrgId().equals(orgId))
        {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(
                WebhookResponse.builder()
                        .callbackUrl(webhook.getCallbackUrl())
                        .id(webhook.getId())
                        .active(webhook.isActive())
                        .createdAt(webhook.getCreatedAt())
                        .build()
        );
    }

}
