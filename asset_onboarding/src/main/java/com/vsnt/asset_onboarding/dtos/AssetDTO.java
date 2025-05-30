package com.vsnt.asset_onboarding.dtos;

import com.vsnt.asset_onboarding.entities.enums.UploadStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetDTO {
    private Long id;
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
