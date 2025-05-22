package com.vsnt.channel_service.exceptions;

import com.vsnt.user.exceptions.APIException;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

public class BadRequestException extends APIException {
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST,new Timestamp(System.currentTimeMillis()));
    }
}
