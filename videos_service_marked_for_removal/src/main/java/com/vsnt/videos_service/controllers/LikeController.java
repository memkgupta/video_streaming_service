package com.vsnt.videos_service.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/like")
public class LikeController {
    @PostMapping("/like")
    public ResponseEntity likeVideo(HttpServletRequest request, @RequestParam String videoId) {
    return ResponseEntity.noContent().build();
    }
    @PostMapping("/removeLike")
    public ResponseEntity removeLikeVideo(HttpServletRequest request, @RequestParam String videoId) {
return ResponseEntity.noContent().build();
    }

}
