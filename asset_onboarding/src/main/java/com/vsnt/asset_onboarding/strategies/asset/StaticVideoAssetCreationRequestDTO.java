package com.vsnt.asset_onboarding.strategies.asset;

import com.vsnt.asset_onboarding.entities.enums.UploadStatus;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Map;

@Data
public class StaticVideoAssetCreationRequestDTO {

    private String fileName;
    private String fileType;
    private long fileSize;
    private String fileUrl;
    private String fileUploadId;
    private UploadStatus uploadStatus;
    private long chunksUploaded;
    private Timestamp startTime;
    private Timestamp endTime;
    private String uploadId;
    private String userId;
    private String key;
    private String url;
    private String videoId;
    private Map<Integer, String> etagMap;


}