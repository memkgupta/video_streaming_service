package com.vsnt.channel_service.controllers;

import com.vsnt.channel_service.entities.Channel;
import com.vsnt.channel_service.payload.channel.ChannelDashboardDTO;
import com.vsnt.channel_service.payload.channel.ChannelPayload;
import com.vsnt.channel_service.services.ChannelService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/channel")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;



    @PostMapping("/")
    public ResponseEntity<ChannelPayload> createChannel(@RequestBody ChannelPayload channelPayload) {
        Channel channel = channelService.createChannel(channelPayload);
        ChannelPayload response =channel.toDTO();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{handle}")
    public ResponseEntity<ChannelPayload> getChannel(@PathVariable String handle) {
        Channel channel = channelService.findByHandle(handle);
        ChannelPayload response =channel.toDTO();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/my-channel")
    public ResponseEntity<ChannelPayload> getMyChannel(HttpServletRequest request) {
        String userId = request.getHeader("X-USER-ID");
        System.out.println("userId "+userId);
        return ResponseEntity.ok(channelService.findByUserId(userId).toDTO());
    }
    @PatchMapping("/{id}")
    public ResponseEntity<ChannelPayload> updateChannel(@PathVariable String id , @RequestBody ChannelPayload channelPayload) {
        Channel channel = channelService.updateChannel(channelPayload,id);
        return ResponseEntity.ok(channel.toDTO());
    }


}
