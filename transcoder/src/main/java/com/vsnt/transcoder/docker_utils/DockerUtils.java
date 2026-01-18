package com.vsnt.transcoder.docker_utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.WaitResponse;
import com.vsnt.transcoder.config.DockerClientSingleton;
import com.vsnt.transcoder.config.KafkaProducer;
import com.vsnt.transcoder.config.Secrets;
import com.vsnt.transcoder.dtos.UpdateRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DockerUtils {
    private String ACCESS_KEY = Secrets.AWS_ACCESS_KEY_ID;
    private String SECRET_KEY=Secrets.AWS_SECRET_KEY;
    private DockerClient dockerClient = DockerClientSingleton.getDockerClient();
    private final KafkaProducer kafkaProducer;
    public DockerUtils(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public void runContainer(String fileKey,String videoId)
    {

        CreateContainerResponse container = dockerClient.createContainerCmd(Secrets.DOCKER_TRANSCODER_CONTAINER_IMAGE)

        .withName("transcoding_image-"+videoId)
                .withEnv(
                        List.of(
                                "ACCESS_KEY="+ACCESS_KEY,
                                "SECRET_KEY="+SECRET_KEY,
                                "BUCKET_NAME="+Secrets.AWS_RAW_BUCKET_NAME,
                                "TRANSCODED_BUCKET_NAME="+Secrets.AWS_TRANSCODED_BUCKET_NAME,
                                "FILE_KEY="+fileKey,
                                "VIDEO_ID="+videoId,
                                "CLOUDFRONT_URL="+Secrets.CLOUD_FRONT_URL,
                                "UPDATE_API_URL=http://host.docker.internal:8082/update"
                        )
                )

                .exec()
                ;

        dockerClient.startContainerCmd(container.getId()).exec();

        kafkaProducer.produce(new UpdateRequestDTO("PROCESSING",videoId));

    }
}
