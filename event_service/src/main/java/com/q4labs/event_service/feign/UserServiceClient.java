package com.q4labs.event_service.feign;

import com.q4labs.event_service.dtos.responses.SubscriptionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user")
public interface UserServiceClient {

    @GetMapping("/v1/authorise/webhook-subscriptions")
    List<SubscriptionResponse> getSubscriptions(@RequestParam("orgId") String orgId , @RequestParam("eventType") String eventType);
}