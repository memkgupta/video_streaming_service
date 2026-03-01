package com.vsnt.transcoder.docker_utils;

public interface ContainerRegistry {
     String getFreeContainerId();
     void removeContainer(String containerId);
     void registerContainer(String containerId);
}

