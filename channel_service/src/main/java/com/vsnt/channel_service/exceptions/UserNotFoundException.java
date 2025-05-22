package com.vsnt.channel_service.exceptions;

import com.vsnt.user.exceptions.APIException;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

public class UserNotFoundException extends APIException {
public UserNotFoundException(String id) {
    super("User with id " + id + " not found", HttpStatus.NOT_FOUND,new Timestamp(System.currentTimeMillis()));
}
}
