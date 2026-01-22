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
        String fileKey = env.get("FILE_KEY").toString();
        String assetId = env.get("ASSET_ID").toString();
        CreateContainerResponse container = dockerClient.createContainerCmd(Secrets.DOCKER_TRANSCODER_CONTAINER_IMAGE)

                .withName("moderation_image-"+assetId)
                .withEnv(
                        List.of(
                                "ACCESS_KEY="+Secrets.AWS_ACCESS_KEY_ID,
                                "SECRET_KEY="+Secrets.AWS_SECRET_KEY,
                                "BUCKET_NAME="+Secrets.AWS_RAW_BUCKET_NAME,
                                "TRANSCODED_BUCKET_NAME="+Secrets.AWS_TRANSCODED_BUCKET_NAME,
                                "FILE_KEY="+fileKey,
                                "ASSET_ID="+assetId,
                                "CLOUDFRONT_URL="+Secrets.CLOUD_FRONT_URL,
                                "UPDATE_API_URL=http://host.docker.internal:8082/update"
                        )
                )

                .exec()
                ;
        dockerClient.startContainerCmd(container.getId()).exec();
    }

}
