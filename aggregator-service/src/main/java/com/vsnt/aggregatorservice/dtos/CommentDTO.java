package com.vsnt.aggregatorservice.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentDTO {
    private String id;
    private String content;
    private Timestamp createdAt;
    private String updatedAt;
    private long likes;
    private long dislikes;
    private String userId;
    private UserDTO user;
    private String replyTo;
    private String parentCommentId;

}

