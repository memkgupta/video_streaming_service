package com.vsnt.aggregatorservice.controllers;

import com.vsnt.aggregatorservice.clients.AssetClient;
import com.vsnt.aggregatorservice.clients.ChannelClient;
import com.vsnt.aggregatorservice.clients.UserClient;
import com.vsnt.aggregatorservice.clients.VideoClient;
import com.vsnt.aggregatorservice.dtos.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/videos")
@Tag(name = "Video API", description = "APIs for video playback and comments aggregation")
public class VideoController {

    private final VideoClient videoClient;
    private final ChannelClient channelClient;
    private final AssetClient assetClient;
    private final UserClient userClient;

    @GetMapping("/watch")
    @Operation(
            summary = "Get video details for playback",
            description = "Returns video metadata, URL, and channel info for the given video ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Video info retrieved",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = VideoPlayerDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Video not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<VideoPlayerDTO> watchVideo(
            HttpServletRequest request,
            @Parameter(description = "ID of the video", required = true)
            @RequestParam String videoId) {

        VideoDTO video = videoClient.getVideo(videoId);
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
    @Operation(
            summary = "Fetch comments for a video",
            description = "Returns paginated comments with user info for a given video ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Comments fetched successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginatedDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<?> getVideoComments(
            HttpServletRequest request,
            @Parameter(description = "Video ID for which comments are to be fetched", required = true)
            @RequestParam String videoId,
            @Parameter(description = "Page number for pagination", required = true)
            @RequestParam int page,
            @Parameter(description = "Page size for pagination", required = true)
            @RequestParam int size) {

        PaginatedDTO<CommentDTO> comments = videoClient.getComments(videoId, page, size);
        List<String> ids = comments.getData().stream().map(CommentDTO::getUserId).distinct().toList();
        List<UserDTO> users = userClient.getAllUser(ids);

        Map<String, UserDTO> map = new HashMap<>();
        users.forEach(u -> map.put(u.getId(), u));

        List<CommentDTO> commentList = comments.getData().stream().map(c -> {
            c.setUser(map.get(c.getUserId()));
            return c;
        }).toList();

        comments.setData(commentList);
        return ResponseEntity.ok(comments);
    }
}
