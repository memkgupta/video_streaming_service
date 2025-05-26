package com.vsnt.aggregatorservice.config;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
@Data
public class CustomFeignException extends RuntimeException {
   private String message;
   private HttpStatus code;
   private Timestamp timestamp;
}
