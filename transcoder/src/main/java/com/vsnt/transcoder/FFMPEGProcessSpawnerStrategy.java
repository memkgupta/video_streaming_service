package com.vsnt.transcoder;

public interface FFMPEGProcessSpawnerStrategy {
    FFMpegProcess spawnProcess(String streamId);
}
