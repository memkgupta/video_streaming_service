package com.vsnt.videos_service.exceptions;


import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

public class VideoNotFoundException extends APIException {
public VideoNotFoundException(String id) {
    super("User with id " + id + " not found", HttpStatus.NOT_FOUND,new Timestamp(System.currentTimeMillis()));
}
}
