package com.vsnt.asset_onboarding.config;

import com.vsnt.asset_onboarding.dtos.UpdateRequestDTO;
import com.vsnt.asset_onboarding.services.AssetService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    private final AssetService assetService;

    public KafkaConsumer(AssetService assetService) {
        this.assetService = assetService;
    }


    @KafkaListener(topics = "video-updates",groupId = "video-updates-consumer")
    public void listen(UpdateRequestDTO updateRequestDTO) {
        System.out.println(updateRequestDTO);
       assetService.updateAssetUrl(updateRequestDTO.getVideoId(),updateRequestDTO.getUrl());
    }
}
