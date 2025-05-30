package com.vsnt.asset_onboarding.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.vsnt.asset_onboarding.dtos.UpdateRequestDTO;
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
            return null;
        }
    }
}
