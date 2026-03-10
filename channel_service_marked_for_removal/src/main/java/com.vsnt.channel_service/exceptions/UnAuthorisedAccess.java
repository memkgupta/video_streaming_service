package com.vsnt.channel_service.exceptions;


import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

public class UnAuthorisedAccess extends APIException {
    public UnAuthorisedAccess(String resourceName , String resourceId) {
        super("Unauthorised access to "+resourceName +"with id "+resourceId);
        status=401;
    }
}
