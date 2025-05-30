package com.vsnt.aggregatorservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
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
    private String status;
    private String thumbnailUrl;
    private String assetId;
    private long likes;
    private long totalComments;
    private long views;
}
