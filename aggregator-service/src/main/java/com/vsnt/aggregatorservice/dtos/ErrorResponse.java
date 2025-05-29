package com.vsnt.aggregatorservice.dtos;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
@Data
public class ErrorResponse {
    private String message;
    private HttpStatus code;
    private Timestamp timestamp;
    private String status;
}
