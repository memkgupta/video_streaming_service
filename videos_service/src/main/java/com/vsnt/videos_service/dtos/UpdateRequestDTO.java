package com.vsnt.videos_service.dtos;

import lombok.Data;

@Data
public class UpdateRequestDTO {
    private String videoId;
    private String url;

    private String timestamp;
    private String status;

    @Override
    public String toString() {
        return "UpdateRequestDTO{" +
                "videoId='" + videoId + '\'' +
                ", url='" + url + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", status='" + status + '\'' +
                '}';
    }


}
