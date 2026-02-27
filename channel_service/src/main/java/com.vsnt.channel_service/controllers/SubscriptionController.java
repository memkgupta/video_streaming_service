package com.vsnt.channel_service.controllers;

import com.vsnt.channel_service.entities.Subscription;
import com.vsnt.channel_service.payload.channel.ChannelPayload;
import com.vsnt.channel_service.payload.subscription.MySubscriptions;
import com.vsnt.channel_service.payload.subscription.SubscriptionDTO;
import com.vsnt.channel_service.services.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscription")
@Tag(name = "Subscription Controller", description = "APIs for managing user subscriptions to channels")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/my-subscriptions")
    @Operation(
            summary = "Get list of subscriptions of logged-in user",
            description = "Returns paginated list of channels the user is subscribed to.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of subscriptions returned successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<MySubscriptions> mySubscriptions(
            HttpServletRequest request,
            @RequestParam int page,
            @RequestParam int size) {

        String userId = request.getHeader("X-USER-ID");

        Page<Subscription> subscriptionPage = subscriptionService.getSubscriptions(userId, page, size);
        int totalPages = subscriptionPage.getTotalPages();
        long totalResults = subscriptionPage.getTotalElements();

        List<ChannelPayload> data = subscriptionPage.getContent().stream().map((s) -> {
            ChannelPayload payload = new ChannelPayload();
            payload.setId(s.getChannel().getId());
            payload.setName(s.getChannel().getName());
            payload.setProfile(s.getChannel().getProfile());
            return payload;
        }).toList();

        MySubscriptions mySubscriptions = new MySubscriptions();
        mySubscriptions.setData(data);
        mySubscriptions.setNextCursor(page == totalPages ? null : page + 1);
        mySubscriptions.setPreviousCursor(page == 0 ? null : page - 1);
        mySubscriptions.setTotalResults(totalResults);

        return ResponseEntity.ok(mySubscriptions);
    }

    @PostMapping("/subscribe/{channelId}")
    @Operation(
            summary = "Subscribe to a channel",
            description = "Subscribes the logged-in user to the specified channel.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully subscribed"),
                    @ApiResponse(responseCode = "404", description = "Channel not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<SubscriptionDTO> subscribe(
            @Parameter(description = "ID of the channel to subscribe to") @PathVariable String channelId,
            HttpServletRequest request) {

        String userId = request.getHeader("X-USER-ID");
        Subscription s = subscriptionService.subscribe(channelId, userId);
        return ResponseEntity.ok(s.toDTO());
    }

    @PostMapping("/unsubscribe/{subscriptionId}")
    @Operation(
            summary = "Unsubscribe from a channel",
            description = "Removes the subscription of the logged-in user from the specified channel.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully unsubscribed"),
                    @ApiResponse(responseCode = "404", description = "Subscription not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<?> unsubscribe(
            @Parameter(description = "ID of the subscription to remove") @PathVariable Long subscriptionId,
            HttpServletRequest request) {

        String userId = request.getHeader("X-USER-ID");
        subscriptionService.unSubscribe(subscriptionId, userId);
        return ResponseEntity.ok(new SubscriptionDTO());
    }
}
