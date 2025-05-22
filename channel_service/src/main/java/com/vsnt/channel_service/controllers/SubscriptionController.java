package com.vsnt.channel_service.controllers;

import com.vsnt.channel_service.entities.Subscription;
import com.vsnt.channel_service.payload.channel.ChannelPayload;
import com.vsnt.channel_service.payload.subscription.MySubscriptions;
import com.vsnt.channel_service.payload.subscription.SubscriptionDTO;
import com.vsnt.channel_service.services.SubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/my-subscriptions")
    public ResponseEntity<MySubscriptions> mySubscriptions(HttpServletRequest request,@RequestParam int page,@RequestParam int size) {
        String userId = request.getHeader("X-USER-ID");

        Page<Subscription> subscriptionPage = subscriptionService.getSubscriptions(userId,page,size);
       int totalPages = subscriptionPage.getTotalPages();
      long totalResults = subscriptionPage.getTotalElements();
     List<ChannelPayload> data= subscriptionPage.getContent().stream().map((s)->{
         ChannelPayload payload = new ChannelPayload();
         payload.setId(s.getChannel().getId());
         payload.setName(s.getChannel().getName());
         payload.setProfile(s.getChannel().getProfile());
          return payload;
      }).toList();
     MySubscriptions mySubscriptions = new MySubscriptions();
     mySubscriptions.setData(data);
    mySubscriptions.setNextCursor(page==totalPages?null:page+1);
    mySubscriptions.setPreviousCursor(page == 0?null:page-1);
    mySubscriptions.setTotalResults(totalResults);
        return ResponseEntity.ok(mySubscriptions);
    }
    @PostMapping("/subscribe/{channelId}")
    public ResponseEntity<SubscriptionDTO> subscribe(@PathVariable String channelId,HttpServletRequest request) {
        String userId = request.getHeader("X-USER-ID");
       Subscription s =  subscriptionService.subscribe(channelId,userId);
        return ResponseEntity.ok(s.toDTO());
    }
    @PostMapping("/unsubscribe/{subscriptionId}")
    public ResponseEntity<?> unsubscribe(@PathVariable Long subscriptionId,HttpServletRequest request) {
        String userId = request.getHeader("X-USER-ID");
        subscriptionService.unSubscribe(subscriptionId,userId);
return ResponseEntity.ok(new SubscriptionDTO());
    }
}
