package com.vsnt.videos_service.controllers;

import com.vsnt.videos_service.dtos.LiveStreamDTO;
import com.vsnt.videos_service.entities.LiveStream;
import com.vsnt.videos_service.services.LiveStreamService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/live")
public class LiveController {
    private final LiveStreamService liveStreamService;

    public LiveController(LiveStreamService liveStreamService) {
        this.liveStreamService = liveStreamService;
    }

    @PostMapping
public LiveStreamDTO startLive(@RequestParam String channelId)
    {
        LiveStream liveStream = liveStreamService.createLiveStream(
                channelId
        );
        return LiveStreamDTO.builder()
                .url(liveStream.getStreamKey())
                .build();
    }
    @GetMapping("/{streamKey}/index.m3u8")
    public ResponseEntity<String> getPlaylist(@PathVariable String streamKey) {

        // Get playlist from service (String format)
        String playlist = liveStreamService.getLivePlaylist(streamKey);

        if (playlist == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/vnd.apple.mpegurl")
                .body(playlist);
    }
    @PostMapping("/{streamKey}/end")
    public ResponseEntity<Void> endStream(@PathVariable String streamKey) {
        liveStreamService.endStream(streamKey);
        return ResponseEntity.noContent().build();
    }
}
