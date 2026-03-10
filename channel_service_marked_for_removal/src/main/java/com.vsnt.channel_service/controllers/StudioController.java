package com.vsnt.channel_service.controllers;

import com.vsnt.channel_service.payload.PaginatedDTO;
import com.vsnt.channel_service.payload.channel.AnalyticsDTO;
import com.vsnt.channel_service.payload.channel.ChannelDashboardDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/studio/{channelId}")
@Tag(name = "Studio", description = "Endpoints for studio dashboard and analytics")
public class StudioController {

    @Operation(
            summary = "Get channel dashboard data",
            description = "Fetches stats and overview data for the studio dashboard of a specific channel"
    )
    @GetMapping("/dashboard")
    public ResponseEntity<ChannelDashboardDTO> getChannelDashboard(
            @Parameter(description = "ID of the channel", example = "cln123xyz")
            @PathVariable String channelId
    ) {
        return ResponseEntity.ok(new ChannelDashboardDTO());
    }

    @Operation(
            summary = "Get channel analytics",
            description = "Provides detailed analytics and metrics for the given channel"
    )
    @GetMapping("/analytics")
    public ResponseEntity<AnalyticsDTO> getChannelAnalytics(
            @Parameter(description = "ID of the channel", example = "cln123xyz")
            @PathVariable String channelId
    ) {
        return ResponseEntity.ok(new AnalyticsDTO());
    }

}
