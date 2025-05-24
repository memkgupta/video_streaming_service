package com.vsnt.channel_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsnt.channel_service.payload.channel.ChannelPayload;
import org.apache.kafka.common.serialization.Deserializer;


public class KafkaDeserializer implements Deserializer<ChannelPayload> {
  private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public ChannelPayload deserialize(String s, byte[] bytes) {
        try{
            ChannelPayload channelPayload = mapper.readValue(bytes, ChannelPayload.class);
            return channelPayload;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
