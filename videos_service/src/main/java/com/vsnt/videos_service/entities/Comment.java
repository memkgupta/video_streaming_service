package com.vsnt.videos_service.entities;

import com.vsnt.videos_service.dtos.CommentDTO;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Comment {
    @Id
    @GeneratedValue
    private long id;
    private String comment;
    private String userId;

    private String videoId;

    private String parentId;

 public CommentDTO toDTO()
 {
     CommentDTO commentDTO = new CommentDTO();
     commentDTO.setComment(comment);
     commentDTO.setUserId(userId);
     commentDTO.setVideoId(videoId);
     commentDTO.setParentId(parentId);
     commentDTO.setId(id);
     return commentDTO;
 }
}
