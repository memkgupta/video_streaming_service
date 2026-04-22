package com.vsnt.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.*;

public class HeartbeatService {

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatService.class);

    private final ScheduledExecutorService scheduler;
    private final HttpClient httpClient;
    private final String registryUrl;
    private final String containerId;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final StreamManager streamManager;

    private ScheduledFuture<?> heartbeatTask;

    public HeartbeatService(HttpClient httpClient,
                            String registryUrl,
                            String containerId,
                            StreamManager streamManager) {
        this.httpClient = httpClient;
        this.registryUrl = registryUrl;
        this.containerId = containerId;
        this.streamManager = streamManager;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start(long intervalSeconds) {
        logger.info("Starting heartbeat service. interval={}s, workerId={}, registry={}",
                intervalSeconds, containerId, registryUrl);

        heartbeatTask = scheduler.scheduleAtFixedRate(
                this::sendHeartbeat,
                0,
                intervalSeconds,
                TimeUnit.SECONDS
        );
    }

    private void sendHeartbeat() {
        logger.debug("Sending heartbeat. workerId={}", containerId);

        try {
            String jsonBody = "{ \"workerId\": \"" + containerId + "\" }";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(registryUrl + "/heartbeat"))
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            long start = System.currentTimeMillis();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            long duration = System.currentTimeMillis() - start;

            logger.debug("Heartbeat response received. status={}, duration={}ms",
                    response.statusCode(), duration);

            if (response.statusCode() == 403) {
                logger.warn("Worker revoked by registry. Initiating STOPPING state. workerId={}", containerId);
                streamManager.stopConsuming();
            }

        } catch (Exception e) {
            logger.error("Heartbeat failed. workerId={}, error={}", containerId, e.getMessage(), e);
        }
    }

    public void stop() {
        logger.info("Stopping heartbeat service. workerId={}", containerId);

        if (heartbeatTask != null) {
            heartbeatTask.cancel(true);
        }

        scheduler.shutdown();
    }
}