package com.vsnt.common_lib.dtos;
import lombok.Data;
@Data
public class ModerationJob {
    private String jobId; // video id
    private String fileKey;

    private long size;

}
