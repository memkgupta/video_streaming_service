package com.vsnt.videos_service.services;

import com.vsnt.videos_service.dtos.CommentDTO;
import com.vsnt.videos_service.entities.Comment;
import com.vsnt.videos_service.entities.Video;
import com.vsnt.videos_service.exceptions.BadRequestException;
import com.vsnt.videos_service.exceptions.InternalServerError;
import com.vsnt.videos_service.exceptions.VideoNotFoundException;
import com.vsnt.videos_service.payloads.PostCommentPayload;
import com.vsnt.videos_service.repositories.CommentRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final VideoService videoService;
    private final CommentRepository commentRepository;

    public Comment postComment(PostCommentPayload commentDTO , String userId,boolean isReply,String parentId) {

            Video video = videoService.getVideo(commentDTO.getVideoId());
            if(video==null)
            {
                throw new VideoNotFoundException(commentDTO.getVideoId());
            }
        Comment comment = new Comment();
            String replyTo = commentDTO.getReplyTo();
            if(isReply)
            {
                if(parentId==null)
                    throw new BadRequestException("Bad request");
                Comment parentComment = getComment(parentId);
                if(parentComment==null)
                {
                    throw new BadRequestException("Bad request");
                }
                comment.setParentId(parentComment.getId());
                if(replyTo!=null)
                {
                    comment.setReplyTo(replyTo);
                }
            }


            comment.setComment(commentDTO.getComment());

            comment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            comment.setVideoId(video.getId());
            comment.setUserId(userId);
           return commentRepository.save(comment);


    }
    public Comment getComment(String commentId) {
            return commentRepository.findById(commentId).orElse(null);
    }
    public Page<Comment> getCommentsOfVideo(String videoId, int page, int size) {
        Specification<Comment> specification = (root, query, cb) -> {
            Predicate byVideoId = cb.equal(root.get("videoId"), videoId);
            Predicate isTopLevel = cb.isNull(root.get("parentId"));
            return cb.and(byVideoId, isTopLevel);
        };            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Comment> comments = commentRepository.findAll(specification,pageable);
            return comments;
    }
    public Page<Comment> getReplies(String parentId, int page, int size) {
            Specification<Comment> specification = (root,query,cb)-> cb.equal(root.get("parentId"), parentId);
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Comment> comments = commentRepository.findAll(specification,pageable);

            return comments;

    }
    public List<Object[]> getReplyCountOfComments(List<String> parentIds)
    {
        return commentRepository.countRepliesForParentIds(parentIds);
    }
}
