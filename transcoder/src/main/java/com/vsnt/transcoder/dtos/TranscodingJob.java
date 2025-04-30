package com.vsnt.transcoder.dtos;


import java.sql.Timestamp;
import java.time.LocalDateTime;

public class TranscodingJob {
    private String jobId;
    private String key;
    private long size;

    public TranscodingJob() {
    }

    public TranscodingJob(String jobId, String key, long size) {
        this.jobId = jobId;
        this.key = key;
        this.size = size;
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



    @Override
    public String toString() {
        return "TranscodingJob{" +
                "jobId='" + jobId + '\'' +
                ", key='" + key + '\'' +
                ", size=" + size +

                '}';
    }
}
