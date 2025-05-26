package com.vsnt.aggregatorservice.clients;

import com.vsnt.aggregatorservice.config.FeignConfig;
import com.vsnt.aggregatorservice.dtos.ChannelDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "channel-service",configuration = FeignConfig.class)
public interface ChannelClient {
    @GetMapping("/channel/my-channel")
    ChannelDTO getMyChannel(@RequestHeader(name = "X-USER-ID") String userId);
    @GetMapping("/channel/{handle}")
    ChannelDTO getChannel(@PathVariable("handle") String handle);
}
