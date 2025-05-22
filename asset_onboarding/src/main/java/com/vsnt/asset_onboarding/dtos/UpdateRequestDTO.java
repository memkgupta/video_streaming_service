package com.vsnt.asset_onboarding.dtos;



import java.sql.Timestamp;

public class UpdateRequestDTO {
    private String videoId;
    private String url;
    private String timestamp;
    private String status;

    @Override
    public String toString() {
        return "UpdateRequestDTO{" +
                "videoId='" + videoId  + '\'' +
                ", url='" + url + '\'' +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                '}';
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
