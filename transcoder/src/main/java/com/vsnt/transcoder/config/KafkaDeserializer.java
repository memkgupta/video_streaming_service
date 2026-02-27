package com.vsnt.transcoder.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsnt.transcoder.dtos.StreamChunk;
import org.apache.kafka.common.serialization.Deserializer;

public class KafkaDeserializer implements Deserializer {
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public StreamChunk deserialize(String s, byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try{
            StreamChunk dto = mapper.readValue(bytes, StreamChunk.class);
            return dto;
        }
        catch(Exception e){
            System.out.println("❌ Failed to deserialize: " + new String(bytes));
            e.printStackTrace();
            throw new RuntimeException(e);
//            throw new InternalServerError("Error de serializing the dto "+ e.getMessage());
        }
    }
}