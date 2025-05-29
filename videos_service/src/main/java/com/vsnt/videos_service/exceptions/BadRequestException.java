package com.vsnt.videos_service.exceptions;


import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

public class BadRequestException extends APIException {
    public BadRequestException(String message) {
        super(message);
        status=400;
    }
}
