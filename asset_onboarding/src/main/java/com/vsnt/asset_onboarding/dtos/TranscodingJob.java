package com.vsnt.asset_onboarding.dtos;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vsnt.asset_onboarding.config.Serializer;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
@JsonSerialize(using = Serializer.class)
public class TranscodingJob  {
    private String jobId;
    private String key;
    private long size;
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
