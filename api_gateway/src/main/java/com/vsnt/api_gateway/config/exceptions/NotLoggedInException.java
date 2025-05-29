package com.vsnt.api_gateway.config.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class NotLoggedInException extends ResponseStatusException {
    public NotLoggedInException(HttpStatusCode status) {
        super(status);
    }
}
