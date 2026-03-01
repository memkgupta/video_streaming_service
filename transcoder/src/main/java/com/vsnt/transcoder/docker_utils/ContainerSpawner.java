package com.vsnt.transcoder.docker_utils;

import java.util.HashMap;
import java.util.Map;

public interface ContainerSpawner {
    public void spawn(String imageName, Map<String,Object> args) throws Exception;
}
