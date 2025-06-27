package com.vsnt.videos_service.controllers;

import com.vsnt.videos_service.dtos.CommentDTO;
import com.vsnt.videos_service.dtos.PaginatedResponse;
import com.vsnt.videos_service.entities.Comment;
import com.vsnt.videos_service.payloads.PostCommentPayload;
import com.vsnt.videos_service.services.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comment")
public class CommentController {

    public static final String X_USER_ID = "X-USER-ID";
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentDTO> postComment(HttpServletRequest request, @RequestBody PostCommentPayload dto,@RequestParam(defaultValue = "false") boolean isReply,@RequestParam(defaultValue = "") String parentId) {
        String userId = request.getHeader(X_USER_ID);
        Comment comment = commentService.postComment(dto, userId,isReply,parentId);
        return ResponseEntity.ok(comment.toDTO());
    }
    @GetMapping
    public ResponseEntity<PaginatedResponse<CommentDTO>> getAllComments(HttpServletRequest request,@RequestParam String videoId,@RequestParam int page , @RequestParam int size) {
        Page<Comment> comments = commentService.getCommentsOfVideo(videoId, page-1, size);
        long totalResults = comments.getTotalElements();
        int totalPages = comments.getTotalPages();
        Integer nextCursor = page == totalPages ?null:page+1;
        Integer previousCursor = page == 0 ? null:page-1;
        List<Object[]> replyCounts = commentService.getReplyCountOfComments(comments.getContent().stream().map(Comment::getId).toList());

        HashMap<String,Long> countMap = new HashMap<>();
        replyCounts.forEach(r->{
            countMap.put((String)r[0],(Long)r[1]);
        });


        List<CommentDTO> commentDTOList = comments.getContent().stream().map(Comment::toDTO).collect(Collectors.toCollection(ArrayList::new));
        commentDTOList.forEach(commentDTO -> {

            commentDTO.setTotalReplies(countMap.getOrDefault(commentDTO.getId(),0L));
        });
        PaginatedResponse<CommentDTO> paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setData(commentDTOList);
        paginatedResponse.setNextCursor(nextCursor);
        paginatedResponse.setPreviousCursor(previousCursor);
        paginatedResponse.setTotalResults(totalResults);

        return ResponseEntity.ok(paginatedResponse);
    }

    @GetMapping("/replies")
    public ResponseEntity<PaginatedResponse<CommentDTO>> getAllReplies(HttpServletRequest request,@RequestParam String parentId,@RequestParam int page , @RequestParam int size) {
        Page<Comment> comments = commentService.getReplies(parentId, page, size);
        long totalResults = comments.getTotalElements();
        int totalPages = comments.getTotalPages();
        Integer nextCursor = page == totalPages ?null:page+1;
        Integer previousCursor = page == 0 ? null:page-1;
        List<CommentDTO> commentDTOList = comments.getContent().stream().map(Comment::toDTO).toList();
        PaginatedResponse<CommentDTO> paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setData(commentDTOList);
        paginatedResponse.setNextCursor(nextCursor);
        paginatedResponse.setPreviousCursor(previousCursor);
        paginatedResponse.setTotalResults(totalResults);

        return ResponseEntity.ok(paginatedResponse);
    }
}
