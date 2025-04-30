package com.vsnt.transcoder.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;

import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import java.time.Duration;


public class DockerClientSingleton {
    private static DockerClient dockerClient;
   static DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
   static DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .sslConfig(config.getSSLConfig())
            .maxConnections(100)
            .connectionTimeout(Duration.ofSeconds(30))
            .responseTimeout(Duration.ofSeconds(45))
            .build();
    public static DockerClient getDockerClient() {

        if (dockerClient == null) {
            dockerClient = DockerClientImpl.getInstance(config, httpClient);
        }
        return dockerClient;
    }
}
