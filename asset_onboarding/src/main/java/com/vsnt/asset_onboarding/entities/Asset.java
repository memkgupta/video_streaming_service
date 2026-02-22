package com.vsnt.asset_onboarding.entities;

import com.vsnt.asset_onboarding.dtos.AssetDTO;
import com.vsnt.asset_onboarding.entities.enums.UploadStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String fileName;
    private String fileType;
    private long fileSize;
    private String storageURL;
    private String fileUploadId;
    @Enumerated(EnumType.STRING)
    private UploadStatus uploadStatus;
    private long chunksUploaded;
    private Timestamp startTime;
    private Timestamp endTime;
    private String uploadId;
    private String key;
    private String cdnURL;
    private UUID mediaId;
    @ElementCollection(fetch = FetchType.EAGER)
    private Map<Integer,String> etagMap;

    public AssetDTO toDTO() {
        AssetDTO dto = new AssetDTO();
        dto.setId(this.id);
        dto.setFileName(this.fileName);
        dto.setFileType(this.fileType);
        dto.setFileSize(this.fileSize);
        dto.setFileUrl(this.storageURL);
        dto.setFileUploadId(this.fileUploadId);
        dto.setUploadStatus(this.uploadStatus);
        dto.setChunksUploaded(this.chunksUploaded);
        dto.setStartTime(this.startTime);
        dto.setEndTime(this.endTime);
        dto.setUploadId(this.uploadId);

        dto.setKey(this.key);
        dto.setUrl(this.cdnURL);


        return dto;
    }
}
