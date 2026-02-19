package com.vsnt.transcoder;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class StreamsTranscodingProcesses {
    private final HashMap<
            String , FFMpegProcess
            >
 processHashMap = new HashMap<>();
    private final FFMPEGProcessSpawnerStrategy spawnerStrategy;

    public StreamsTranscodingProcesses(FFMPEGProcessSpawnerStrategy spawnerStrategy) {
        this.spawnerStrategy = spawnerStrategy;
    }

    public void add(FFMpegProcess process , String streamId)
    {
        processHashMap.put(streamId, process);
    }
    public FFMpegProcess get(String streamId)
    {
        return processHashMap.get(streamId);
    }
    public boolean containsProcess(String streamId)
    {
        return processHashMap.containsKey(streamId);
    }
    public void spawnProcess(String streamId)
    {
    FFMpegProcess process = spawnerStrategy.spawnProcess(streamId);
    processHashMap.put(streamId, process);
    }
}
