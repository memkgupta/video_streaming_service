package com.vsnt.videos_service.config;

import com.vsnt.videos_service.dtos.StreamSegmentUpdateDTO;
import com.vsnt.videos_service.dtos.UpdateRequestDTO;
import com.vsnt.videos_service.dtos.UpdateType;
import com.vsnt.videos_service.entities.VideoUploadStatusEnum;
import com.vsnt.videos_service.services.LiveStreamService;
import com.vsnt.videos_service.services.VideoService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Component
public class KafkaConsumer {

    private final VideoService videoService;
    private final LiveStreamService liveStreamService;

    public KafkaConsumer(VideoService videoService,
                         LiveStreamService liveStreamService) {
        this.videoService = videoService;
        this.liveStreamService = liveStreamService;
    }

    @KafkaListener(
            topics = "video-updates",
            groupId = "video-updates-consumer-status",
            containerFactory = "updateRequestKafkaListenerFactory"
    )
    public void listen(UpdateRequestDTO updateRequestDTO) {

        System.out.println("Received update request: " + updateRequestDTO);

        if(updateRequestDTO.getType().equals(UpdateType.STATUS_UPDATE)) {

            videoService.updateVideoUploadStatus(
                    updateRequestDTO.getVideoId(),
                    VideoUploadStatusEnum.valueOf(updateRequestDTO.getStatus())
            );

        } else if (updateRequestDTO.getType().equals(UpdateType.TRANSCRIPT_UPDATE)) {

            videoService.updateTranscript(
                    updateRequestDTO.getVideoId(),
                    updateRequestDTO.getTranscriptURL()
            );

        } else if(updateRequestDTO.getType().equals(UpdateType.MODERATION_UPDATE)) {

            videoService.updateModerationSummary(
                    updateRequestDTO.getModerationResult()
            );
        }
    }

    @KafkaListener(
            topics = "stream-chunk-updates",
            groupId = "stream-update-consumer",
            containerFactory = "streamSegmentKafkaListenerFactory"
    )
    public void listenChunkUpdate(StreamSegmentUpdateDTO streamSegmentUpdateDTO) {

        System.out.println("Received chunk update: " + streamSegmentUpdateDTO);

        liveStreamService.updateSegement(streamSegmentUpdateDTO);
    }
}
