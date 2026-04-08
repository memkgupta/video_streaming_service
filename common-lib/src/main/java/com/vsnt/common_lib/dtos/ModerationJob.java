package com.vsnt.common_lib.dtos;
import lombok.Data;
@Data
public class ModerationJob {
    private String asset_id; // video id
    private String job_id;

    private String content_url;
    private long size;
    private long duration;
}
