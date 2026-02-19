package com.vsnt.videos_service.entities;

import com.vsnt.videos_service.dtos.ModerationFlag;
import com.vsnt.videos_service.dtos.ModerationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
public class ModerationSummary {
    @EmbeddedId
    private ModerationId id;
    private ModerationStatus status;
    private double confidenceScore;
    private List<ModerationFlag> flags;
    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String,String> metadata;
    @ManyToOne(optional = false)
    @MapsId("videoId")
    @JoinColumn(name = "video_id")
    private Video video;
    private String moderationResultKey;

}
