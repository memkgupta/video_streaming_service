package com.vsnt.asset_onboarding.dtos;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vsnt.asset_onboarding.config.Serializer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@JsonSerialize(using = Serializer.class)
@Builder
@Getter
@Setter
public class TranscodingJob  {
    private String jobId;
    private String key;
    private long size;
    private byte[] encryptionKey;
    private ModerationResult moderationResult;
    private List<AssetChunk> chunks;



    @Override
    public String toString() {
        return "TranscodingJob{" +
                "jobId='" + jobId + '\'' +
                ", key='" + key + '\'' +
                ", size=" + size +
                '}';
    }
}
