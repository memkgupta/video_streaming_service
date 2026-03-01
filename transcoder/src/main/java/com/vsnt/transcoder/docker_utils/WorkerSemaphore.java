package com.vsnt.transcoder.docker_utils;

public interface WorkerSemaphore {
    boolean tryAcquire() ;
    void release();


}
