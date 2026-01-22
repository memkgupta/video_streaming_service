package com.vsnt.asset_onboarding.config;

import com.vsnt.asset_onboarding.dtos.ModerationStatus;
import com.vsnt.asset_onboarding.dtos.TranscodingJob;
import com.vsnt.asset_onboarding.dtos.UpdateRequestDTO;
import com.vsnt.asset_onboarding.dtos.UpdateType;
import com.vsnt.asset_onboarding.entities.Asset;
import com.vsnt.asset_onboarding.services.AssetService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    private final AssetService assetService;
    private final TranscodingJobMessageProducer transcodingJobMessageProducer;
    public KafkaConsumer(AssetService assetService, TranscodingJobMessageProducer transcodingJobMessageProducer) {
        this.assetService = assetService;
        this.transcodingJobMessageProducer = transcodingJobMessageProducer;
    }


    @KafkaListener(topics = "video-updates",groupId = "video-updates-consumer")
    public void listen(UpdateRequestDTO updateRequestDTO) {
        System.out.println(updateRequestDTO);
        if(updateRequestDTO.getType().equals(UpdateType.STATUS_UPDATE))
        {
            assetService.updateAssetUrl(updateRequestDTO.getVideoId(),updateRequestDTO.getUrl());

        }
        else if(updateRequestDTO.getType().equals(UpdateType.MODERATION_UPDATE))
        {
            if(updateRequestDTO.getModerationResult().getStatus().equals(ModerationStatus.APPROVED))
            {
                Asset asset = assetService.getAssetByVideoId(updateRequestDTO.getVideoId());
                if(asset==null)
                {
                    throw new RuntimeException("Asset Not Found");
                }
                TranscodingJob job = new TranscodingJob();
                job.setJobId(
                        updateRequestDTO.getVideoId()
                );
                job.setKey(asset.getKey());
                job.setModerationResult(updateRequestDTO.getModerationResult());
                job.setSize(asset.getFileSize());
                transcodingJobMessageProducer.sendMessage(
                        job
                );
            }


        }
    }

}
