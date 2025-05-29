package com.vsnt.api_gateway.config.exceptions;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorResponse = super.getErrorAttributes(request, options);
        HttpStatus status = HttpStatus.valueOf((Integer) errorResponse.get("status"));
        String message = errorResponse.get("message").toString();
        if(message==null)
        {
            switch (status)
            {
                case BAD_REQUEST:
                    errorResponse.put("message", "Bad Request");
                    break;
                case UNAUTHORIZED:
                    errorResponse.put("message", "Unauthorized Access");
                    break;
                case FORBIDDEN:
                    errorResponse.put("message", "Forbidden");
                    break;
                case NOT_FOUND:
                    errorResponse.put("message", "Not Found");
                    break;
                case INTERNAL_SERVER_ERROR:
                    errorResponse.put("message", "Internal Server Error");
                    break;
                default:
                    errorResponse.put("message", "Something went wrong");
            }
        }

        return errorResponse;
    }
}
