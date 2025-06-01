package com.vsnt.aggregatorservice.clients;

import com.vsnt.aggregatorservice.config.FeignConfig;
import com.vsnt.aggregatorservice.dtos.CommentDTO;
import com.vsnt.aggregatorservice.dtos.PaginatedDTO;
import com.vsnt.aggregatorservice.dtos.VideoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "videos-service",configuration = FeignConfig.class)
public interface VideoClient {
    @GetMapping("/videos")
    public PaginatedDTO<VideoDTO> getVideos(@RequestParam Map<String,String> params, @RequestHeader(name = "X-USER-ID") String userId);
    @GetMapping("/videos/watch")
    VideoDTO getVideo(@RequestParam String v);
    @GetMapping("/comment")
    PaginatedDTO<CommentDTO> getComments(@RequestParam String videoId,@RequestParam int page , @RequestParam int size);

}
