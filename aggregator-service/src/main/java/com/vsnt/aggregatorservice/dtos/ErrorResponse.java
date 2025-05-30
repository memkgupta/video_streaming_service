package com.vsnt.aggregatorservice.dtos;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
@Data
public class ErrorResponse {
    private String message;

    private Timestamp timestamp;
    private int status;
}
