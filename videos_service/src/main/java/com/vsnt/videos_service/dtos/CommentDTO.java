package com.vsnt.videos_service.dtos;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class CommentDTO {
    private String id;
    private String content;
    private Timestamp createdAt;
    private String updatedAt;
    private long likes;
    private long dislikes;
    private String userId;
    private String replyTo;
    private String parentCommentId;
    private long totalReplies;
}
