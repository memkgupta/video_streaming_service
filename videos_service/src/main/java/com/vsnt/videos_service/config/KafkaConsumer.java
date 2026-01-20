package com.vsnt.videos_service.config;

import com.vsnt.videos_service.dtos.UpdateRequestDTO;
import com.vsnt.videos_service.dtos.UpdateType;
import com.vsnt.videos_service.entities.VideoUploadStatusEnum;
import com.vsnt.videos_service.services.VideoService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    private final VideoService videoService;

    public KafkaConsumer(VideoService videoService) {
        this.videoService = videoService;
    }

    @KafkaListener(topics = "video-updates",groupId = "video-updates-consumer-status")
    public void listen(UpdateRequestDTO updateRequestDTO) {
        if(updateRequestDTO.getType().equals(UpdateType.STATUS_UPDATE))
        {
            videoService.updateVideoUploadStatus(updateRequestDTO.getVideoId(), VideoUploadStatusEnum.valueOf(updateRequestDTO.getStatus()));

        } else if (updateRequestDTO.getType().equals(UpdateType.TRANSCRIPT_UPDATE)) {
            videoService.updateTranscript(updateRequestDTO.getVideoId(),updateRequestDTO.getTranscriptURL());
        }
        else if(updateRequestDTO.getType().equals(UpdateType.MODERATION_UPDATE)) {
            videoService.updateModerationSummary(updateRequestDTO.getModerationResult());
        }
    }
}
