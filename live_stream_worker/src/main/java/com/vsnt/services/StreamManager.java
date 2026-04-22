package com.vsnt.services;

public interface StreamManager {
    void stopConsuming();
    boolean canConsume();
}