package com.vsnt.videos_service.entities;

import com.vsnt.videos_service.dtos.CommentDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String comment;
    private String userId;
    private long likes;
    private long dislikes;

    private String videoId;

    private String parentId;
    private Timestamp createdAt;
    private String replyTo;
 public CommentDTO toDTO()
 {
     CommentDTO commentDTO = new CommentDTO();
     commentDTO.setContent(comment);
     commentDTO.setUserId(userId);
     commentDTO.setLikes(likes);
     commentDTO.setDislikes(dislikes);
     commentDTO.setParentCommentId(parentId);
     commentDTO.setCreatedAt(createdAt);
     commentDTO.setReplyTo(replyTo);
     commentDTO.setId(id);
     return commentDTO;
 }
}
