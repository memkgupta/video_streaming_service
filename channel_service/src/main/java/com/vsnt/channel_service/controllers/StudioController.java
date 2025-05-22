package com.vsnt.channel_service.controllers;

import com.vsnt.channel_service.payload.PaginatedDTO;
import com.vsnt.channel_service.payload.channel.AnalyticsDTO;
import com.vsnt.channel_service.payload.channel.ChannelDashboardDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/studio/{channelId}")
public class StudioController {
    @GetMapping("/dashboard")
    public ResponseEntity<ChannelDashboardDTO> getChannelDashboard(@PathVariable String channelId) {
        return ResponseEntity.ok(new ChannelDashboardDTO());
    }
    @GetMapping("/analytics")
    public ResponseEntity<AnalyticsDTO> getChannelAnalytics(@PathVariable String channelId)
    {
        return ResponseEntity.ok(new AnalyticsDTO());
    }

}
