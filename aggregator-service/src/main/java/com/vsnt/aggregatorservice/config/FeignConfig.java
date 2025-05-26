package com.vsnt.aggregatorservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsnt.aggregatorservice.dtos.ErrorResponse;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class FeignConfig {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Bean
    public ErrorDecoder errorDecoder() {
    return (s, response) -> {
        String body = null;
        try {
            body = new String(response.body().asInputStream().readAllBytes());
            ErrorResponse error = objectMapper.readValue(body, ErrorResponse.class);
            CustomFeignException exception = new CustomFeignException();
            exception.setTimestamp(error.getTimestamp());
            exception.setCode(error.getCode());
            exception.setMessage(error.getMessage());
            return exception ;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    };
    }
}
