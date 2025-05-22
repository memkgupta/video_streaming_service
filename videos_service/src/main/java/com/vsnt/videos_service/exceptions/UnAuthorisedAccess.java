package com.vsnt.videos_service.exceptions;

import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

public class UnAuthorisedAccess extends APIException {
    public UnAuthorisedAccess(String resourceName , String resourceId) {
        super("Unauthorised access to "+resourceName +"with id "+resourceId, HttpStatus.UNAUTHORIZED,new Timestamp(System.currentTimeMillis()));
    }
}
