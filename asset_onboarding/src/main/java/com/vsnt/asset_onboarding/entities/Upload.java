package com.vsnt.asset_onboarding.entities;

import com.vsnt.asset_onboarding.entities.enums.UploadStatus;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Map;

@Entity
public class Upload {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String fileName;
    private String fileType;
    private long fileSize;
    private String fileUrl;
    private String fileUploadId;
    @Enumerated(EnumType.STRING)
    private UploadStatus uploadStatus;
    private long chunksUploaded;
    private Timestamp startTime;
    private Timestamp endTime;
    private String uploadId;
    private String userId;
    private String key;

    @Override
    public String toString() {
        return "Upload{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                ", fileUrl='" + fileUrl + '\'' +
                ", fileUploadId='" + fileUploadId + '\'' +
                ", uploadStatus=" + uploadStatus +
                ", chunksUploaded=" + chunksUploaded +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", uploadId='" + uploadId + '\'' +
                ", userId='" + userId + '\'' +
                ", key='" + key + '\'' +
                ", etagMap=" + etagMap +
                '}';
    }

    @ElementCollection(fetch = FetchType.EAGER)

    private Map<Integer,String> etagMap;

    public String getKey() {
        return key;
    }

    public Map<Integer, String> getEtagMap() {
        return etagMap;
    }

    public void setEtagMap(Map<Integer, String> etagMap) {
        this.etagMap = etagMap;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getFileUploadId() {
        return fileUploadId;
    }

    public void setFileUploadId(String fileUploadId) {
        this.fileUploadId = fileUploadId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public UploadStatus getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(UploadStatus uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public long getChunksUploaded() {
        return chunksUploaded;
    }

    public void setChunksUploaded(long chunksUploaded) {
        this.chunksUploaded = chunksUploaded;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }
}
