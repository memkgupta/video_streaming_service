package com.vsnt.videos_service.payloads;

import lombok.Data;

@Data
public class PostCommentPayload {
    private String comment;
    private String userId;
    private String videoId;
    private Long id;
    private String parentCommentId;
}
