package com.vsnt.aggregatorservice.controllers;

import com.vsnt.aggregatorservice.clients.ChannelClient;
import com.vsnt.aggregatorservice.clients.VideoClient;
import com.vsnt.aggregatorservice.config.APIException;
import com.vsnt.aggregatorservice.config.CustomFeignException;
import com.vsnt.aggregatorservice.dtos.ChannelDTO;
import com.vsnt.aggregatorservice.dtos.PaginatedDTO;
import com.vsnt.aggregatorservice.dtos.VideoDTO;
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
    @GetMapping("/channel/videos")
public ResponseEntity<?> getMyVideos(HttpServletRequest request, @RequestParam String page, @RequestParam String size )
{
    try{
        ChannelDTO channelDTO = channelClient.getMyChannel(request.getHeader("X-USER-ID"));
        Map<String,String> map = new HashMap<>();
        map.put("page",page);
        map.put("size",size);
        map.put("channelId",channelDTO.getId());
        PaginatedDTO<VideoDTO> videos = videoClient.getVideos(map,request.getHeader("X-USER-ID"));

        return ResponseEntity.ok(videos);
    } catch (CustomFeignException e) {

       throw new APIException(e.getMessage(),e.getStatus().value());
    }


}

}
