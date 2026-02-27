package com.vsnt.videos_service.dtos;

import com.vsnt.videos_service.entities.ModerationSummary;
import lombok.Data;

@Data
public class UpdateRequestDTO {
    private String videoId;
    private String url;

    private String timestamp;
    private String status;
    private UpdateType type;
    private String transcriptURL;
    private ModerationResult moderationResult;

    @Override
    public String toString() {
        return "UpdateRequestDTO{" +
                "videoId='" + videoId + '\'' +
                ", url='" + url + '\'' +
                "type="+type+
                ", timestamp='" + timestamp + '\'' +
                ", status='" + status + '\'' +
                '}';
    }


}
