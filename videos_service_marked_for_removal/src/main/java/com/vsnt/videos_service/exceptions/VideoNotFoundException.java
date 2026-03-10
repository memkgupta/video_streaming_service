package com.vsnt.videos_service.exceptions;


import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

public class VideoNotFoundException extends APIException {
public VideoNotFoundException(String id) {
    super("Video with id " + id + " not found");
    status=404;
}
}
