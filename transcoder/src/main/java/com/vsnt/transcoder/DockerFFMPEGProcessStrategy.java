package com.vsnt.transcoder;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import com.vsnt.transcoder.config.DockerClientSingleton;
import com.vsnt.transcoder.config.Secrets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;

@Component

public class DockerFFMPEGProcessStrategy
        implements FFMPEGProcessSpawnerStrategy {



    private final DockerClient dockerClient =
            DockerClientSingleton.getDockerClient();

    @Override
    public FFMpegProcess spawnProcess(String streamId) {

        try {

//            String containerName =
//                    "live_transcoder-" + streamId;



            CreateContainerResponse container =
                    dockerClient.createContainerCmd("live_transcoder:latest")
                            .withName("live_transcoder-" + streamId)

                            // VERY IMPORTANT
                            .withAttachStdin(true)
                            .withAttachStdout(true)
                            .withAttachStderr(true)
                            .withStdinOpen(true)
                            .withTty(false)
                            .withHostConfig(
                                    HostConfig.newHostConfig()
                                            .withNetworkMode("url_shortener_backend_app-net") // ← your network name
                                            .withAutoRemove(true)
                            )
                            .withEnv(
                                    "STREAM_KEY=" + streamId,
                                    "S3_BUCKET=" + Secrets.AWS_TRANSCODED_BUCKET_NAME,
                                    "AWS_ACCESS_KEY=" + Secrets.AWS_ACCESS_KEY_ID,
                                    "AWS_SECRET_KEY=" + Secrets.AWS_SECRET_KEY,
                                    "KAFKA_BOOTSTRAP=kafka:9092",
                                    "KAFKA_TOPIC=stream-chunk-updates",
                                    "CDN_BASE_URL=" + Secrets.CLOUD_FRONT_URL
                            )
                            .exec();

            String containerId =
                    container.getId();

            // Start container


            // Attach STDIN
            PipedOutputStream stdinPipe =
                    new PipedOutputStream();

            PipedInputStream stdinInput =
                    new PipedInputStream(stdinPipe);

            dockerClient.attachContainerCmd(containerId)
                    .withStdIn(stdinInput)
                    .withStdOut(true)
                    .withStdErr(true)
                    .withFollowStream(true)
                    .exec(new ResultCallback.Adapter<>());
            dockerClient.startContainerCmd(containerId).exec();
            return new LocalDockerFFmpegProcess(containerId, stdinPipe, stdinInput, dockerClient);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to spawn FFmpeg container",
                    e
            );
        }
    }
}
