package com.vsnt.common_lib.utils;

import java.util.Map;

public interface DockerContainerSpawner {
    void spawn(Map<String,Object> env);
}
