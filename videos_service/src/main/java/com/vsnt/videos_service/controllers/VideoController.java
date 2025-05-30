package com.vsnt.videos_service.controllers;

import com.vsnt.videos_service.dtos.PaginatedResponse;
import com.vsnt.videos_service.dtos.VideoDTO;
import com.vsnt.videos_service.entities.Video;
import com.vsnt.videos_service.exceptions.VideoNotFoundException;
import com.vsnt.videos_service.services.VideoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoController {

    public static final String USER_ID = "X-USER-ID";
    private final VideoService videoService;
    @PostMapping("/newDraft")
    public ResponseEntity<VideoDTO> createVideoDraft
            ( HttpServletRequest request,@RequestParam String channelId) {
        String userId = request.getHeader(USER_ID);
        Video video = videoService.createVideoDraft(userId,channelId);
        return ResponseEntity.ok(video.toDTO());
    }
@PutMapping("/fill-details")
public ResponseEntity<VideoDTO> fillDetails(
        HttpServletRequest request,@RequestBody VideoDTO videoDTO,@RequestParam String videoId
)
{
    String userId = request.getHeader(USER_ID);
    Video video = videoService.fillDetails(videoDTO,userId,videoId);
    return ResponseEntity.ok(video.toDTO());
}
    @GetMapping("/watch")
    public ResponseEntity<VideoDTO> watchVideo(HttpServletRequest request,@RequestParam String v)
    {
        String userId = request.getHeader(USER_ID);
        Video video = videoService.getVideo(v);
        if(video==null)
        {
            System.out.println("Video null");
            throw new VideoNotFoundException("Video not found");
        }

        return ResponseEntity.ok(video.toDTO());
    }
    @PostMapping("/publish")
    public ResponseEntity publishVideo(HttpServletRequest request,@RequestParam String videoId)
    {
        String userId = request.getHeader(USER_ID);
       videoService.publishVideo(videoId,userId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("")
    public ResponseEntity<PaginatedResponse<VideoDTO>> getVideos(HttpServletRequest request, @RequestParam Map<String,String> params )
    {
        System.out.println("Request came here");
        String userId = request.getHeader(USER_ID);
        int pageNumber = Integer.parseInt(params.getOrDefault("page","1"))-1;
        int pageSize = Integer.parseInt(params.getOrDefault("size","100"));
        params.remove("page");
        params.remove("size");
        String sortBy = params.getOrDefault("sortBy","uploadedAt");
        String order = params.getOrDefault("order","DESC");
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Video> videos = videoService.getAllVideos(params,pageable);
        int totalPages = videos.getTotalPages();
        long totalResults = videos.getTotalElements();

        Integer nextCursor = pageNumber == totalPages?null:pageNumber + 1;
        Integer prevCursor = pageNumber == 0?null:pageNumber - 1;
        PaginatedResponse<VideoDTO> paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setData(videos.stream().map(Video::toDTO).toList());
        paginatedResponse.setNextCursor(nextCursor);
        paginatedResponse.setPreviousCursor(prevCursor);
        paginatedResponse.setTotalResults(totalResults);
        return ResponseEntity.ok(paginatedResponse);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<VideoDTO> updateVideo(HttpServletRequest request, @PathVariable String id, @RequestBody VideoDTO videoDTO) {
        String userId = request.getHeader(USER_ID);
        Video video = videoService.updateVideo(id, videoDTO, userId);
        return ResponseEntity.ok(video.toDTO());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<VideoDTO> deleteVideo(HttpServletRequest request, @PathVariable String id) {
        String userId = request.getHeader(USER_ID);
        videoService.deleteVideo(id, userId);
        return ResponseEntity.ok().build();
    }

}
