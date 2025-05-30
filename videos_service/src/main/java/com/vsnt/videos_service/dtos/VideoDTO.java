package com.vsnt.videos_service.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vsnt.videos_service.entities.VideoUploadStatusEnum;
import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    private long likes=0;
    private long totalComments=0;
    private long views=0;
}
