package com.vsnt.user.exceptions;

public class BadCredentialsException extends APIException{
    public BadCredentialsException(String message) {
        super(message);
        status = 401;
    }
}
