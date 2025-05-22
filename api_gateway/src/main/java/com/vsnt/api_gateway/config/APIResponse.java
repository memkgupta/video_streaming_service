package com.vsnt.api_gateway.config;

import java.util.Map;

public class APIResponse {
    private boolean success;
    private Map<String,Object> data ;
    private String message;
    private String error;

    @Override
    public String toString() {
        return "APIResponse{" +
                "success=" + success +
                ", data=" + data +
                ", message='" + message + '\'' +
                ", error='" + error + '\'' +
                '}';
    }

    public APIResponse() {
    }

    public APIResponse(boolean success, Map<String,Object> data, String message, String error) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Map<String,Object> getData() {
        return data;
    }

    public void setData(Map<String,Object> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
