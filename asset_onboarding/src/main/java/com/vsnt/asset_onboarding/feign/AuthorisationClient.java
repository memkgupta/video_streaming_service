package com.vsnt.asset_onboarding.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "user" , url = "http://localhost:8080/api/v1/authorise")
public interface AuthorisationClient {
    @GetMapping("/membership")
     boolean isMember(@RequestParam("userId") String userId ,  @RequestParam("groupId") UUID groupId);
}
