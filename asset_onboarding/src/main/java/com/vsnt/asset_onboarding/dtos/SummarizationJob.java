package com.vsnt.asset_onboarding.dtos;

import java.util.List;

public class SummarizationJob {
    private String jobId; // video id
    private String key; // for fetching the video from s3
    private long size;
    private List<AssetChunk>  chunks;

    public List<AssetChunk> getChunks() {
        return chunks;
    }

    public void setChunks(List<AssetChunk> chunks) {
        this.chunks = chunks;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

}
