package com.vsnt.videos_service.config;

import com.vsnt.videos_service.dtos.UpdateRequestDTO;
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
videoService.updateVideoUploadStatus(updateRequestDTO.getVideoId(), VideoUploadStatusEnum.valueOf(updateRequestDTO.getStatus()));
    }
}
