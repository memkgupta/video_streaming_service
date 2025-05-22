package com.vsnt.videos_service.dtos;

import lombok.Data;

@Data
public class CommentDTO {
    private long id;
    private String comment;
    private String userId;

    private String videoId;

    private String parentId;
}
