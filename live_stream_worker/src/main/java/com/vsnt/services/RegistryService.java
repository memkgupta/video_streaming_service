package com.vsnt.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Objects;

public class RegistryService {

    private static final Logger log = LoggerFactory.getLogger(RegistryService.class);

    private final HttpClient httpClient;
    private String containerId;
    private final String serviceURL;

    public RegistryService(HttpClient httpClient, String serviceURL) {
        this.httpClient = Objects.requireNonNull(httpClient);
        this.serviceURL = Objects.requireNonNull(serviceURL);
        System.out.println("RegistryService initialized with serviceURL={} "+ serviceURL );
    }

    // Call once when container starts
    public void getId() {
        log.info("Attempting to register container...");

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serviceURL + "/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{}"))
                    .build();

            log.debug("Sending register request to {}", serviceURL + "/register");

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            log.debug("Received response status={} body={}", response.statusCode(), response.body());

            if (response.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> responseMap = objectMapper.readValue(
                        response.body(),
                        new TypeReference<Map<String, Object>>() {}
                );

                this.containerId = responseMap.get("workerId").toString();

                log.info("Successfully registered container. containerId={}", containerId);
            } else {
                log.error("Failed to register container. status={} body={}",
                        response.statusCode(), response.body());
                throw new RuntimeException("Failed to register container");
            }

        } catch (IOException e) {
            log.error("IO error while registering container", e);
            throw new RuntimeException("Error while registering container", e);
        } catch (InterruptedException e) {
            log.error("Thread interrupted while registering container", e);
            Thread.currentThread().interrupt(); // important
            throw new RuntimeException("Interrupted while registering container", e);
        }
    }

    public void notifyRegistry(String mediaId, String assetId) {
      System.out.println(String.format("Notifying registry")
                );

        if (containerId == null) {
            System.out.println("Container ID is null. getId() was not called.");
            throw new IllegalStateException("Container ID not initialized. Call getId() first.");
        }

        try {
            String payload = String.format(
                    "{ \"workerId\": \"%s\", \"streamKey\": \"%s\", \"assetId\": \"%s\", \"status\": \"TRANSCODING\" }",
                    containerId, mediaId, assetId
            );

            System.out.println("Notifying registry with this payload: " + payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serviceURL + "/notify"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println("Notifiying repsonse "+response.body());

            if (response.statusCode() != 200) {
                System.out.println("Failed to notify registry with this payload: " + response.body());
                throw new RuntimeException("Failed to notify registry");
            }

            log.info("Successfully notified registry for mediaId={}", mediaId);

        } catch (IOException e) {
            log.error("IO error while notifying registry", e);
            throw new RuntimeException("Error while notifying registry", e);
        } catch (InterruptedException e) {
            log.error("Thread interrupted while notifying registry", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while notifying registry", e);
        }
    }

    public String getContainerId() {
        log.debug("Fetching containerId={}", containerId);
        return containerId;
    }
}