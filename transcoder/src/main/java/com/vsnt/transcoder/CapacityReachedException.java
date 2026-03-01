package com.vsnt.transcoder;

public class CapacityReachedException extends RuntimeException {
    public CapacityReachedException(String message) {
        super(message);
    }
}
