package com.vsnt.videos_service.dtos;

import com.vsnt.videos_service.entities.VideoUploadStatusEnum;
import lombok.Data;

import java.sql.Timestamp;
@Data
public class VideoDTO {
    private String id;
    private String title;
    private boolean isPublished;
    private String description;
    private String channelId;
    private String userId;
    private Timestamp uploadedAt;
    private long duration;
    private String fileUploadId;
    private VideoUploadStatusEnum status;
    private String thumbnailUrl;
    private String assetId;
}
