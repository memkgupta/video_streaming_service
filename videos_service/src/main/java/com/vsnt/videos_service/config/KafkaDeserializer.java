package com.vsnt.videos_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsnt.videos_service.dtos.UpdateRequestDTO;
import com.vsnt.videos_service.exceptions.InternalServerError;
import org.apache.kafka.common.serialization.Deserializer;

public class KafkaDeserializer implements Deserializer {
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public UpdateRequestDTO deserialize(String s, byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try{
            UpdateRequestDTO dto = mapper.readValue(bytes, UpdateRequestDTO.class);
       return dto;
        }
        catch(Exception e){
            System.out.println("❌ Failed to deserialize: " + new String(bytes));
            e.printStackTrace();
            throw new InternalServerError("Error de serializing the dto "+ e.getMessage());
        }
    }
}
