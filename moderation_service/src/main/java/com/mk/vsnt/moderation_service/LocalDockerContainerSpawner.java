package com.mk.vsnt.moderation_service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.vsnt.common_lib.Secrets;
import com.vsnt.common_lib.config.DockerClientSingleton;
import com.vsnt.common_lib.utils.DockerContainerSpawner;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;


@Component
public class LocalDockerContainerSpawner implements DockerContainerSpawner {
    private final DockerClient dockerClient = DockerClientSingleton.getDockerClient();
    @Override
    public void spawn(Map<String, Object> env) {
        String fileURL = env.get("PRESIGNED_URL").toString();
        String videoId = env.get("VIDEO_ID").toString();
        long assetSize = Long.parseLong(env.get("ASSET_SIZE").toString());
        CreateContainerResponse container = dockerClient.createContainerCmd(Secrets.DOCKER_MODERATION_WORKER_CONTAINER_NAME)

                .withName("moderation_worker-"+videoId)
                .withEnv(
                        List.of(
                                "AWS_ACCESS_ID="+Secrets.AWS_ACCESS_KEY_ID,
                                "AWS_SECRET="+Secrets.AWS_SECRET_KEY,
                                "S3_ASSET_BUCKET="+Secrets.AWS_RAW_BUCKET_NAME,
                                "S3_REPORT_BUCKET="+Secrets.AWS_TRANSCODED_BUCKET_NAME,
                                "JOB_ID="+videoId,
                                "ASSET_URL="+fileURL,
                                "ASSET_SIZE="+assetSize,
                                "KAFKA_HOST="+Secrets.KAFKA_HOST

                        )
                )

                .exec()
                ;
        dockerClient.startContainerCmd(container.getId()).exec();
    }

}
