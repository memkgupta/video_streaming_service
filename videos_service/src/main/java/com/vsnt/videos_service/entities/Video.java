package com.vsnt.videos_service.entities;

import com.vsnt.videos_service.dtos.VideoDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String title;
    private String description;
    private String channelId;
    private String userId;
    private Timestamp uploadedAt;
    private long duration;
    private VideoVisibilityStatusEnum visibilityStatus;
    @Enumerated(EnumType.STRING)
    private VideoUploadStatusEnum status;
    private String thumbnailUrl;
    private String assetId;
public VideoDTO toDTO()
{
    VideoDTO dto = new VideoDTO();
    dto.setId(id);
    dto.setTitle(title);
    dto.setDescription(description);
    dto.setChannelId(channelId);
    dto.setUserId(userId);
    dto.setUploadedAt(uploadedAt);
    dto.setDuration(duration);
    dto.setStatus(status);
    dto.setThumbnailUrl(thumbnailUrl);
    dto.setAssetId(assetId);
    return dto;
}
}
