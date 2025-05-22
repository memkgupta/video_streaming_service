package com.vsnt.asset_onboarding.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.vsnt.asset_onboarding.dtos.UpdateRequestDTO;
import org.apache.kafka.common.serialization.Serializer;

public class KafkaSerializer implements Serializer<UpdateRequestDTO> {
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public byte[] serialize(String s, UpdateRequestDTO updateRequestDTO) {
        try {
            return mapper.writeValueAsBytes(updateRequestDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
