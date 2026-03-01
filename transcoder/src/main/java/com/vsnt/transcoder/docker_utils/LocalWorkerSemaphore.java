package com.vsnt.transcoder.docker_utils;

import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LocalWorkerSemaphore implements WorkerSemaphore {

    private final Semaphore semaphore;

    public LocalWorkerSemaphore() {
        this.semaphore = new Semaphore(10, true);
        // true = fairness (FIFO)
    }

    @Override
    public boolean tryAcquire() {
        return semaphore.tryAcquire();
    }

    @Override
    public void release() {
        semaphore.release();

    }

}