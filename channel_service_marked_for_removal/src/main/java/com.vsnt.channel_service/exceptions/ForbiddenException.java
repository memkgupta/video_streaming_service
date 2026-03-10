package com.vsnt.channel_service.exceptions;

public class ForbiddenException extends APIException{
    public ForbiddenException(String message) {
        super(message);
        status=403;
    }
}
