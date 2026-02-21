package com.vsnt.asset_onboarding.dtos;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vsnt.asset_onboarding.config.Serializer;
import lombok.Builder;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@JsonSerialize(using = Serializer.class)
@Builder
public class TranscodingJob  {
    private String jobId;
    private String key;
    private long size;
    private ModerationResult moderationResult;
    private List<AssetChunk> chunks;

    public List<AssetChunk> getChunks() {
        return chunks;
    }

    public void setChunks(List<AssetChunk> chunks) {
        this.chunks = chunks;
    }

    public ModerationResult getModerationResult() {
        return moderationResult;
    }

    public void setModerationResult(ModerationResult moderationResult) {
        this.moderationResult = moderationResult;
    }
    //    private LocalDateTime time;

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

    @Override
    public String toString() {
        return "TranscodingJob{" +
                "jobId='" + jobId + '\'' +
                ", key='" + key + '\'' +
                ", size=" + size +
                '}';
    }
}
