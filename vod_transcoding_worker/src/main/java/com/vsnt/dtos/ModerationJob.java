package com.vsnt.dtos;

public class ModerationJob {
    private String asset_id; // video id
    private String job_id;

    private String content_url;
    private long size;
    private long duration;

    public ModerationJob(String assetId, String jobId, String url, long size, long segmentDuration) {
        this.asset_id = assetId;
        this.job_id = jobId;
        this.content_url = url;
        this.size = size;
        this.duration = segmentDuration;
    }

    public String getAsset_id() {
        return asset_id;
    }

    public void setAsset_id(String asset_id) {
        this.asset_id = asset_id;
    }

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    public String getContent_url() {
        return content_url;
    }

    public void setContent_url(String content_url) {
        this.content_url = content_url;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
