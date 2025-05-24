package com.vsnt.channel_service.exceptions;


import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

public class ChannelNotFoundException extends APIException {
public ChannelNotFoundException(String id) {
    super("Channel with id " + id + " not found", HttpStatus.NOT_FOUND,new Timestamp(System.currentTimeMillis()));
}
}
