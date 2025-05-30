package com.vsnt.aggregatorservice.clients;

import com.vsnt.aggregatorservice.dtos.AssetDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "asset-onboarding")
public interface AssetClient {
    @GetMapping("/assets/{id}")
    public AssetDTO getAssetById(@PathVariable("id") String id);
}
