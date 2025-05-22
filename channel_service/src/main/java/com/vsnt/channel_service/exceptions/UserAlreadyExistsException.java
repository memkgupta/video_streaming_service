package com.vsnt.channel_service.exceptions;

import com.vsnt.user.exceptions.APIException;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

public class UserAlreadyExistsException extends APIException {
    public UserAlreadyExistsException(String email) {
        super("User already exists with email "+email, HttpStatus.CONFLICT,new Timestamp(System.currentTimeMillis()));
    }
}
