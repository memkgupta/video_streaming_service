package com.vsnt.aggregatorservice.controllers;

import com.vsnt.aggregatorservice.clients.AssetClient;
import com.vsnt.aggregatorservice.clients.ChannelClient;
import com.vsnt.aggregatorservice.clients.UserClient;
import com.vsnt.aggregatorservice.clients.VideoClient;
import com.vsnt.aggregatorservice.dtos.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/videos")
public class VideoController {
    private final VideoClient videoClient;
    private final ChannelClient channelClient;
    private final AssetClient assetClient;
    private final UserClient userClient;
    @GetMapping("/watch")
    public ResponseEntity<VideoPlayerDTO> watchVideo(HttpServletRequest request, @RequestParam String videoId) {
        VideoDTO video = videoClient.getVideo(videoId);
        System.out.println(video.getChannelId());
        ChannelDTO channelDTO = channelClient.getChannel(video.getChannelId());
        AssetDTO assetDTO = assetClient.getAssetById(video.getAssetId());
        VideoPlayerDTO videoPlayerDTO = VideoPlayerDTO.builder()
                .duration(video.getDuration())
                .url(assetDTO.getUrl())
                .likes(video.getLikes())
                .views(video.getViews())
                .totalComments(video.getTotalComments())
                .description(video.getDescription())
                .uploadedAt(video.getUploadedAt())
                .channelDetails(ChannelDTO.builder()
                        .id(channelDTO.getId())
                        .name(channelDTO.getName())
                        .subscribersCount(channelDTO.getSubscribersCount())
                        .profile(channelDTO.getProfile())
                        .build())
                .build();
        return ResponseEntity.ok(videoPlayerDTO);
    }
    @GetMapping("/comment")
    public ResponseEntity<?> getVideoComments(HttpServletRequest request, @RequestParam String videoId,@RequestParam int page , @RequestParam int size) {
        PaginatedDTO<CommentDTO> comments = videoClient.getComments(videoId,page,size);
        List<String> ids = comments.getData().stream()
                .map(CommentDTO::getUserId)
                .distinct()
                .toList();
        List<UserDTO> users = userClient.getAllUser(ids);
        Map<String,UserDTO> map = new HashMap<>();
        users.forEach(u -> map.put(u.getId(), u));

        List<CommentDTO> commentList = comments.getData().stream().map(c->{
            c.setUser(map.get(c.getUserId()));
            return c;
        }).toList();
        comments.setData(commentList);
        return ResponseEntity.ok(comments);

    }
}
