package com.vsnt.user.exceptions;

import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

public class UserNotFoundException extends APIException {
public UserNotFoundException(String id) {
    super("User with id " + id + " not found");
    status=404;
}
}
