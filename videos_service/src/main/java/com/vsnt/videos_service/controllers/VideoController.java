package com.vsnt.videos_service.controllers;

import com.vsnt.videos_service.dtos.PaginatedResponse;
import com.vsnt.videos_service.dtos.VideoDTO;
import com.vsnt.videos_service.entities.Video;
import com.vsnt.videos_service.exceptions.VideoNotFoundException;
import com.vsnt.videos_service.services.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
@Tag(name = "Video Controller", description = "APIs for video creation, update, and retrieval")
public class VideoController {

    public static final String USER_ID = "X-USER-ID";
    private final VideoService videoService;

    @PostMapping("/newDraft")
    @Operation(
            summary = "Create new video draft",
            description = "Creates an empty draft video for a channel",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Draft created", content = @Content(schema = @Schema(implementation = VideoDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    public ResponseEntity<VideoDTO> createVideoDraft(
            HttpServletRequest request,
            @RequestParam String channelId) {
        String userId = request.getHeader(USER_ID);
        Video video = videoService.createVideoDraft(userId, channelId);
        return ResponseEntity.ok(video.toDTO());
    }

    @PutMapping("/fill-details")
    @Operation(
            summary = "Fill video details",
            description = "Updates the draft with title, description, tags, etc.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Video updated", content = @Content(schema = @Schema(implementation = VideoDTO.class)))
            }
    )
    public ResponseEntity<VideoDTO> fillDetails(
            HttpServletRequest request,
            @RequestBody VideoDTO videoDTO,
            @RequestParam String videoId) {
        String userId = request.getHeader(USER_ID);
        Video video = videoService.getVideo(v);
        if(video==null)
        {

            throw new VideoNotFoundException("Video not found");
        }

    @GetMapping("/watch")
    @Operation(
            summary = "Watch video",
            description = "Fetches a video's metadata for playback",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Video fetched", content = @Content(schema = @Schema(implementation = VideoDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Video not found")
            }
    )
    public ResponseEntity<VideoDTO> watchVideo(
            HttpServletRequest request,
            @RequestParam(name = "v") String videoId) {
        String userId = request.getHeader(USER_ID);
        Video video = videoService.getVideo(videoId);
        if (video == null) throw new VideoNotFoundException("Video not found");
        return ResponseEntity.ok(video.toDTO());
    }

    @PostMapping("/publish")
    @Operation(
            summary = "Publish video",
            description = "Publishes a draft video and makes it live",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Published successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    public ResponseEntity<Void> publishVideo(
            HttpServletRequest request,
            @RequestParam String videoId) {
        String userId = request.getHeader(USER_ID);
        videoService.publishVideo(videoId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/feed")
    @Operation(
            summary = "Get video feed",
            description = "Fetches paginated list of published videos with filters like sort, channel, tags, etc.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Feed fetched", content = @Content(schema = @Schema(implementation = PaginatedResponse.class)))
            }
    )
    public ResponseEntity<PaginatedResponse<VideoDTO>> getVideos(
            HttpServletRequest request,
            @RequestParam Map<String, String> params) {

        String userId = request.getHeader(USER_ID);
        int pageNumber = Integer.parseInt(params.getOrDefault("page", "1")) - 1;
        int pageSize = Integer.parseInt(params.getOrDefault("size", "100"));

        params.remove("page");
        params.remove("size");

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Video> videos = videoService.getAllVideos(params, pageable);
        int totalPages = videos.getTotalPages();
        long totalResults = videos.getTotalElements();
        Integer nextCursor = pageNumber == totalPages ? null : pageNumber + 1;
        Integer prevCursor = pageNumber == 0 ? null : pageNumber - 1;

        PaginatedResponse<VideoDTO> paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setData(videos.stream().map(Video::toDTO).toList());
        paginatedResponse.setNextCursor(nextCursor);
        paginatedResponse.setPreviousCursor(prevCursor);
        paginatedResponse.setTotalResults(totalResults);

        return ResponseEntity.ok(paginatedResponse);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Update video",
            description = "Updates an existing video by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Video updated", content = @Content(schema = @Schema(implementation = VideoDTO.class)))
            }
    )
    public ResponseEntity<VideoDTO> updateVideo(
            HttpServletRequest request,
            @PathVariable String id,
            @RequestBody VideoDTO videoDTO) {
        String userId = request.getHeader(USER_ID);
        Video video = videoService.updateVideo(id, videoDTO, userId);
        return ResponseEntity.ok(video.toDTO());
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete video",
            description = "Deletes a video by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Deleted successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    public ResponseEntity<Void> deleteVideo(
            HttpServletRequest request,
            @PathVariable String id) {
        String userId = request.getHeader(USER_ID);
        videoService.deleteVideo(id, userId);
        return ResponseEntity.ok().build();
    }
}
