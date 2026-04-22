package com.vsnt.services;

import com.vsnt.VideoTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public class DefaultStreamManager implements StreamManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultStreamManager.class);

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

            logger.warn("StreamManager transitioning to STOPPING (revocation in progress)");

            new Thread(() -> {
                try {
                    logger.info("Stopping all active streams...");

                    long start = System.currentTimeMillis();

                    // 1) stop all active streams
                    transcoder.stopAllStreams();

                    long duration = System.currentTimeMillis() - start;
                    logger.info("All streams stopped successfully in {} ms", duration);

                } catch (Exception e) {
                    logger.error("Error while stopping streams", e);

                } finally {
                    // 2) resume intake
                    state.set(State.RUNNING);
                    logger.info("StreamManager transitioned back to RUNNING");
                }
            }, "revoke-cleanup").start();

        } else {
            logger.debug("stopConsuming() called but already in STOPPING state");
        }
    }

    @Override
    public boolean canConsume() {
        State currentState = state.get();

        if (currentState != State.RUNNING) {
            logger.debug("canConsume() = false (state={})", currentState);
        }

        return currentState == State.RUNNING;
    }
}