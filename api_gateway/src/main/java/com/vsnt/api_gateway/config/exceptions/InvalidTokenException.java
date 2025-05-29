package com.vsnt.api_gateway.config.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class InvalidTokenException extends ResponseStatusException {
    public InvalidTokenException(HttpStatusCode status) {
        super(status);
    }
}
