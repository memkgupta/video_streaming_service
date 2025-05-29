package com.vsnt.videos_service.services;

import com.vsnt.videos_service.dtos.CommentDTO;
import com.vsnt.videos_service.entities.Comment;
import com.vsnt.videos_service.entities.Video;
import com.vsnt.videos_service.exceptions.InternalServerError;
import com.vsnt.videos_service.exceptions.VideoNotFoundException;
import com.vsnt.videos_service.payloads.PostCommentPayload;
import com.vsnt.videos_service.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final VideoService videoService;
    private final CommentRepository commentRepository;

    public Comment postComment(PostCommentPayload commentDTO , String userId) {

            Video video = videoService.getVideo(commentDTO.getVideoId());
            if(video==null)
            {
                throw new VideoNotFoundException(commentDTO.getVideoId());
            }
            Comment comment = new Comment();
            comment.setComment(commentDTO.getComment());
            comment.setParentId(comment.getParentId());
            comment.setVideoId(video.getId());
            comment.setUserId(userId);
           return commentRepository.save(comment);


    }
    public Comment getComment(Long commentId) {
            return commentRepository.findById(commentId).orElse(null);
    }
    public Page<Comment> getCommentsOfVideo(String videoId, int page, int size) {
            Specification<Comment> specification = (root,query,cb)-> cb.equal(root.get("videoId"), videoId);
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Comment> comments = commentRepository.findAll(specification,pageable);
            return comments;
    }
    public Page<Comment> getReplies(String parentId, int page, int size) {
            Specification<Comment> specification = (root,query,cb)-> cb.equal(root.get("parentId"), parentId);
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Comment> comments = commentRepository.findAll(specification,pageable);
            return comments;

    }
}
