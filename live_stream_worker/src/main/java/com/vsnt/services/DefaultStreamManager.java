package com.vsnt.services;

import com.vsnt.VideoTranscoder;

import java.util.concurrent.atomic.AtomicReference;

public class DefaultStreamManager implements StreamManager {

    public enum State {
        RUNNING,
        STOPPING
    }

    private final VideoTranscoder transcoder;
    private final AtomicReference<State> state = new AtomicReference<>(State.RUNNING);

    public DefaultStreamManager(VideoTranscoder transcoder) {
        this.transcoder = transcoder;
    }

    @Override
    public void stopConsuming() {
        // transition RUNNING -> STOPPING (idempotent)
        if (state.compareAndSet(State.RUNNING, State.STOPPING)) {
            System.out.println("StreamManager: STOPPING (revoke in progress)");

            // async cleanup so we don't block caller (e.g., heartbeat thread)
            new Thread(() -> {
                try {
                    // 1) stop all active streams
                    transcoder.stopAllStreams();

                } finally {
                    // 2) resume intake
                    state.set(State.RUNNING);
                    System.out.println("StreamManager: back to RUNNING");
                }
            }, "revoke-cleanup").start();
        }
    }

    public boolean canConsume() {
        return state.get() == State.RUNNING;
    }
}