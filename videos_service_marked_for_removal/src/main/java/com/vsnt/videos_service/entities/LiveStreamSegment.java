package com.vsnt.videos_service.entities;

import jakarta.persistence.*;
import lombok.Data;

import javax.swing.text.Segment;
import java.sql.Timestamp;

@Entity
@Data
public class LiveStreamSegment {
    @EmbeddedId
    private SegmentId id;
    private String liveStreamId;
    private Timestamp createdAt;
    private String url;
    private Long startDuration;
    private Long endDuration;
    private Long duration;
}
