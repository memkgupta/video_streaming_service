package com.vsnt.channel_service.controllers;

import com.vsnt.channel_service.entities.Channel;
import com.vsnt.channel_service.exceptions.ChannelNotFoundException;
import com.vsnt.channel_service.payload.PaginatedDTO;
import com.vsnt.channel_service.payload.channel.ChannelDashboardDTO;
import com.vsnt.channel_service.payload.channel.ChannelPayload;
import com.vsnt.channel_service.services.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Channel Management", description = "APIs related to Channel operations like create, update, fetch.")
@RestController
@RequestMapping("/channel")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @Operation(
            summary = "Create a new channel",
            description = "Creates a new channel based on the provided payload.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Channel created successfully",
                            content = @Content(schema = @Schema(implementation = ChannelPayload.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping("/")
    public ResponseEntity<ChannelPayload> createChannel(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Payload to create a new channel", required = true,
                    content = @Content(schema = @Schema(implementation = ChannelPayload.class))
            )
            ChannelPayload channelPayload) {
        Channel channel = channelService.createChannel(channelPayload);
        return ResponseEntity.ok(channel.toDTO());
    }

    @Operation(
            summary = "Get a channel by handle",
            description = "Fetch channel details using the channel handle",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Channel found",
                            content = @Content(schema = @Schema(implementation = ChannelPayload.class))),
                    @ApiResponse(responseCode = "404", description = "Channel not found")
            }
    )
    @GetMapping("/{handle}")
    public ResponseEntity<ChannelPayload> getChannel(
            @Parameter(description = "Unique channel handle", required = true)
            @PathVariable String handle) {
        Channel channel = channelService.findByHandle(handle);
        if (channel == null) throw new ChannelNotFoundException(handle);
        return ResponseEntity.ok(channel.toDTO());
    }

    @Operation(
            summary = "Get my channel",
            description = "Fetch the channel associated with the authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User's channel details",
                            content = @Content(schema = @Schema(implementation = ChannelPayload.class)))
            }
    )
    @GetMapping("/my-channel")
    public ResponseEntity<ChannelPayload> getMyChannel(
            @Parameter(hidden = true) HttpServletRequest request) {
        String userId = request.getHeader("X-USER-ID");
        return ResponseEntity.ok(channelService.findByUserId(userId).toDTO());
    }

    @Operation(
            summary = "Update a channel",
            description = "Update an existing channel with the given ID and payload",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Channel updated successfully",
                            content = @Content(schema = @Schema(implementation = ChannelPayload.class))),
                    @ApiResponse(responseCode = "404", description = "Channel not found")
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<ChannelPayload> updateChannel(
            @Parameter(description = "Channel ID to update", required = true)
            @PathVariable String id,
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated channel payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ChannelPayload.class))
            )
            ChannelPayload channelPayload) {
        Channel channel = channelService.updateChannel(channelPayload, id);
        return ResponseEntity.ok(channel.toDTO());
    }
}
