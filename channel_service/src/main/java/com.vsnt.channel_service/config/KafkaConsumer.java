package com.vsnt.channel_service.config;

import com.vsnt.channel_service.payload.channel.ChannelPayload;
import com.vsnt.channel_service.services.ChannelService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    private final ChannelService channelService;

    public KafkaConsumer(ChannelService channelService) {
        this.channelService = channelService;
    }

    @KafkaListener(topics = "create-channel" ,groupId = "channel-consumer-group")
    public void consume(ChannelPayload message)
    {
        try {
            channelService.createChannel(message);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
