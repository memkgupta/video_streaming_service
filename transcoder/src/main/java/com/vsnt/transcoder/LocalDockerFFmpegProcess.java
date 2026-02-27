package com.vsnt.transcoder;

import com.github.dockerjava.api.DockerClient;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;

public class LocalDockerFFmpegProcess implements FFMpegProcess{

    private final String containerId;
    private final OutputStream stdin;
    private final DockerClient dockerClient;
    private final PipedInputStream stdinInput;
    public LocalDockerFFmpegProcess(
            String containerId,
            OutputStream stdin,
            PipedInputStream stdinInput, // ← add this
            DockerClient dockerClient) {
        this.containerId = containerId;
        this.stdin = stdin;
        this.stdinInput = stdinInput;  // ← prevents GC
        this.dockerClient = dockerClient;
    }
    public void write(byte[] chunk) throws IOException {

        stdin.write(chunk);
        stdin.flush();
    }

    public void stop() throws IOException {

        stdin.close();

        dockerClient.stopContainerCmd(containerId).exec();

        dockerClient.removeContainerCmd(containerId)
                .withForce(true)
                .exec();
    }

    public String getContainerId() {
        return containerId;
    }

    @Override
    public boolean stdin(byte[] data, String streamId) {
        try{
            write(data);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
}
