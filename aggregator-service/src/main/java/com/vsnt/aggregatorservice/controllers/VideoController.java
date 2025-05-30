package com.vsnt.aggregatorservice.controllers;

import com.vsnt.aggregatorservice.clients.AssetClient;
import com.vsnt.aggregatorservice.clients.ChannelClient;
import com.vsnt.aggregatorservice.clients.VideoClient;
import com.vsnt.aggregatorservice.dtos.AssetDTO;
import com.vsnt.aggregatorservice.dtos.ChannelDTO;
import com.vsnt.aggregatorservice.dtos.VideoDTO;
import com.vsnt.aggregatorservice.dtos.VideoPlayerDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/videos")
public class VideoController {
    private final VideoClient videoClient;
    private final ChannelClient channelClient;
    private final AssetClient assetClient;
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
                .channelDetails(ChannelDTO.builder()
                        .id(channelDTO.getId())
                        .name(channelDTO.getName())
                        .subscribersCount(channelDTO.getSubscribersCount())
                        .profile(channelDTO.getProfile())
                        .build())
                .build();
        return ResponseEntity.ok(videoPlayerDTO);
    }
}
