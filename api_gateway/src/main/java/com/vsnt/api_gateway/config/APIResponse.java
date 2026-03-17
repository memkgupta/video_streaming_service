package com.vsnt.api_gateway.config;

import java.util.Map;

public class APIResponse {
    private boolean success;
    private Object data ;
    private String message;
    private Object error;

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
    public static APIResponse error(Object body) {
        APIResponse apiResponse = new APIResponse();
        apiResponse.setSuccess(false);
        apiResponse.setData(null);
        apiResponse.setMessage("Error occured");
        apiResponse.setError(body);
        return apiResponse;
    }
    public static APIResponse success(Object body) {
        APIResponse apiResponse = new APIResponse();
        apiResponse.setSuccess(true);
        apiResponse.setData(body);
        apiResponse.setMessage("Success");
        apiResponse.setError(null);
        return apiResponse;
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
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Object getError() {
        return error;
    }
    public void setError(Object error) {
        this.error = error;
    }
}
