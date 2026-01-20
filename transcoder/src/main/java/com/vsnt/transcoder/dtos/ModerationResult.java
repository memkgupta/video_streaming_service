package com.vsnt.transcoder.dtos;


import java.util.List;
import java.util.Map;

public class ModerationResult {
    private String moderationResultKey;
    private ModerationStatus status;
    private double confidenceScore;
    private List<ModerationFlag> flags;
    private Map<String,Object> metadata;
    private String videoId;

    public String getModerationResultKey() {
        return moderationResultKey;
    }

    public ModerationStatus getStatus() {
        return status;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public List<ModerationFlag> getFlags() {
        return flags;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public String getVideoId() {
        return videoId;
    }
}
