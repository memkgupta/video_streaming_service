package com.vsnt.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.sql.Timestamp;

public class UserAlreadyExistsException extends APIException{
    public UserAlreadyExistsException(String email) {
        super("User already exists with email "+email, HttpStatus.CONFLICT,new Timestamp(System.currentTimeMillis()));
    }
}
