package com.vsnt.dtos;

public class TranscodingJob  {
    private String jobId;
    private String key;
    private long size;
    private long duration;
    private String encryptionKey;
    private String assetId;

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
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

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

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

