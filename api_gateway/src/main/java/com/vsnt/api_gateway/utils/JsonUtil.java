package com.vsnt.api_gateway.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public  class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, Object> toMap(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON to Map", e);
        }
    }
}
