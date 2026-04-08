package com.vsnt.asset_onboarding.dtos;


import com.vsnt.asset_onboarding.moderation.ModerationResult;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Builder
@Getter
@Setter
public class TranscodingJob  {
    private String jobId;
    private String key;
    private long size;
    private String encryptionKey;
    private String assetId;
    private ModerationResult moderationResult;
    private List<AssetChunk> chunks;



    @Override
    public String toString() {
        return "TranscodingJob{" +
                "jobId='" + jobId + '\'' +
                ", key='" + key + '\'' +
                ", size=" + size +
                ", encryptionKey=" + new String(encryptionKey) +
                '}';
    }
}
