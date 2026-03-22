package com.vsnt.transcoder.docker_utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.vsnt.transcoder.config.DockerClientSingleton;
import com.vsnt.transcoder.config.Secrets;
import com.vsnt.transcoder.dtos.UpdateRequestDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
@Component
public class LocalContainerSpawner implements ContainerSpawner {
    private String ACCESS_KEY = Secrets.AWS_ACCESS_KEY_ID;
    private String SECRET_KEY=Secrets.AWS_SECRET_KEY;
    private DockerClient dockerClient = DockerClientSingleton.getDockerClient();
    @Override
    public void spawn(String imageName, Map<String, Object> args) throws Exception {

        String mediaId = (String)args.get("mediaId");
        String assetId = (String)args.get("assetId");
        String assetKey  = (String)args.get("assetKey");
        String encryptionKey = (String)args.get("encryptionKey");
        CreateContainerResponse container = dockerClient.createContainerCmd(Secrets.DOCKER_TRANSCODER_CONTAINER_IMAGE)

                .withName("transcoding_image-"+assetId)
                .withNetworkMode("q4-video_app-net")
                .withEnv(
                        List.of(
                                "ACCESS_KEY="+ACCESS_KEY,
                                "SECRET_KEY="+SECRET_KEY,
                                "BUCKET_NAME="+Secrets.AWS_RAW_BUCKET_NAME,
                                "TRANSCODED_BUCKET_NAME="+Secrets.AWS_TRANSCODED_BUCKET_NAME,
                                "FILE_KEY="+assetKey,
                                "MEDIA_ID="+mediaId,
                                "MEDIA_TYPE="+"STATIC",
                                "ASSET_ID="+assetId,
                                "ENCRYPTION_KEY="+new String(encryptionKey),
                                "CLOUDFRONT_URL="+Secrets.CLOUD_FRONT_URL,
                                "KAFKA_BROKERS="+"kafka:9092",
                                "UPDATE_TOPIC_NAME="+"asset-transcoding-updates",
                                "FINISH_TOPIC_NAME="+"asset-transcoding-finish"
                        )
                )
                .exec()
                ;

        dockerClient.startContainerCmd(container.getId()).exec();
    }

}
