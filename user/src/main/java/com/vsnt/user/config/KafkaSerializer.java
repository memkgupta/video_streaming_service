package com.vsnt.user.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsnt.user.payload.ChannelPayload;
import org.apache.kafka.common.serialization.Serializer;

public class KafkaSerializer implements Serializer<ChannelPayload> {
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public byte[] serialize(String s, ChannelPayload channelPayload) {
        try{
            byte[] bytes = mapper.writeValueAsBytes(channelPayload);
            return bytes;

        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
