package com.vsnt.aggregatorservice.dtos;




import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AssetDTO {
    private Long id;
    private String fileName;
    private String fileType;
    private long fileSize;
    private String fileUrl;
    private String fileUploadId;
    private String uploadStatus;
    private Timestamp startTime;
    private Timestamp endTime;
    private String userId;
    private String url;
    private String videoId;




}
