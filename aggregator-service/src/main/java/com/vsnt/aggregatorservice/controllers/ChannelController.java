package com.vsnt.aggregatorservice.controllers;

import com.vsnt.aggregatorservice.clients.ChannelClient;
import com.vsnt.aggregatorservice.clients.VideoClient;
import com.vsnt.aggregatorservice.config.APIException;
import com.vsnt.aggregatorservice.config.CustomFeignException;
import com.vsnt.aggregatorservice.dtos.ChannelDTO;
import com.vsnt.aggregatorservice.dtos.PaginatedDTO;
import com.vsnt.aggregatorservice.dtos.VideoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelClient channelClient;
    private final VideoClient videoClient;

    @Operation(
            summary = "Fetch all videos for the current user's channel",
            description = "Retrieves a paginated list of videos for the authenticated user's channel using X-USER-ID header."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched videos",
                    content = @Content(schema = @Schema(implementation = PaginatedDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/channel/videos")
    public ResponseEntity<?> getMyVideos(
            HttpServletRequest request,

            @Parameter(description = "Page number for pagination", example = "0")
            @RequestParam String page,

            @Parameter(description = "Page size for pagination", example = "10")
            @RequestParam String size
    ) {
        try {
            String userId = request.getHeader("X-USER-ID");
            ChannelDTO channelDTO = channelClient.getMyChannel(userId);

            Map<String, String> map = new HashMap<>();
            map.put("page", page);
            map.put("size", size);
            map.put("channelId", channelDTO.getId());

            PaginatedDTO<VideoDTO> videos = videoClient.getVideos(map, userId);

            return ResponseEntity.ok(videos);
        } catch (CustomFeignException e) {
            throw new APIException(e.getMessage(), e.getStatus().value());
        }
    }
}
