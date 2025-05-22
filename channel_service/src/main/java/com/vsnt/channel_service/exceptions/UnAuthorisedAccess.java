package com.vsnt.channel_service.exceptions;

import com.vsnt.user.exceptions.APIException;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

public class UnAuthorisedAccess extends APIException {
    public UnAuthorisedAccess(String resourceName , String resourceId) {
        super("Unauthorised access to "+resourceName +"with id "+resourceId, HttpStatus.UNAUTHORIZED,new Timestamp(System.currentTimeMillis()));
    }
}
