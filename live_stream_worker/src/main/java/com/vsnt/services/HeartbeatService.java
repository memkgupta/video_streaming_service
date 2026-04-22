package com.vsnt.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class HeartbeatService {

    private final ScheduledExecutorService scheduler;
    private final HttpClient httpClient;
    private final String registryUrl;
    private final String containerId;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // abstraction to control worker streams
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
        heartbeatTask = scheduler.scheduleAtFixedRate(
                this::sendHeartbeat,
                0,
                intervalSeconds,
                TimeUnit.SECONDS
        );
    }

    private void sendHeartbeat() {
        System.out.println("Trying to send heartbeat");
        try {
            String jsonBody = "{ \"workerId\": \"" + containerId + "\" }";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(registryUrl + "/heartbeat"))
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() == 403) {
                streamManager.stopConsuming();
            }



        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Heartbeat error: " + e.getMessage());
        }
    }


    public void stop() {
        if (heartbeatTask != null) {
            heartbeatTask.cancel(true);
        }
        scheduler.shutdown();
    }
}