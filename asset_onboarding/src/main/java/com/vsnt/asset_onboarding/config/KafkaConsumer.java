package com.vsnt.asset_onboarding.config;

import com.vsnt.asset_onboarding.dtos.UpdateRequestDTO;
import com.vsnt.asset_onboarding.services.AssetService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    private final AssetService assetService;
    public KafkaConsumer(AssetService assetService) {
        this.assetService = assetService;
    }
    @KafkaListener(topics = "updates",groupId = "consumer-group-id")
    public void listen(Message<UpdateRequestDTO> message) {
       assetService.updateAssetStatus(Long.parseLong(message.getPayload().getUploadId()),message.getPayload().getStatus());
    }
}
