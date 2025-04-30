package com.vsnt.asset_onboarding.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsnt.asset_onboarding.dtos.UpdateRequestDTO;
import org.apache.kafka.common.serialization.Deserializer;

public class KafkaDeserializer implements Deserializer<UpdateRequestDTO> {
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public UpdateRequestDTO deserialize(String s, byte[] bytes) {
        try{
            if(bytes == null){
                return null;
            }
           return mapper.readValue(bytes, UpdateRequestDTO.class);
        }
        catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Error de serializing the dto", e);
        }

    }
}
