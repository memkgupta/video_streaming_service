package com.vsnt.asset_onboarding.dtos;



import com.vsnt.asset_onboarding.moderation.ModerationResult;

public class UpdateRequestDTO {
    private String videoId;
    private String url;
    private String timestamp;
    private String status;
    private UpdateType type;
    private String transcriptURL;
    private ModerationResult moderationResult;
    @Override
    public String toString() {
        return "UpdateRequestDTO{" +
                "videoId='" + videoId  + '\'' +
                ", url='" + url + '\'' +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                ", type=" + type +
                '}';
    }

    public UpdateType getType() {
        return type;
    }

    public void setType(UpdateType type) {
        this.type = type;
    }

    public String getTranscriptURL() {
        return transcriptURL;
    }

    public void setTranscriptURL(String transcriptURL) {
        this.transcriptURL = transcriptURL;
    }

    public ModerationResult getModerationResult() {
        return moderationResult;
    }

    public void setModerationResult(ModerationResult moderationResult) {
        this.moderationResult = moderationResult;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String uploadId) {
        this.videoId = uploadId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
