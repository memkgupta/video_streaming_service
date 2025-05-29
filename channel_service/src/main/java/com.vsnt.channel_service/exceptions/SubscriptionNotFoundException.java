package com.vsnt.channel_service.exceptions;

public class SubscriptionNotFoundException extends APIException{
    public SubscriptionNotFoundException(String message) {
        super(message);
        status=404;
    }
}
