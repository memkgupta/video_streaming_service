package com.vsnt.transcoder.docker_utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.vsnt.transcoder.config.DockerClientSingleton;
import com.vsnt.transcoder.config.Secrets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DockerUtils {
    private String ACCESS_KEY = Secrets.AWS_SECRET_KEY;
    private String SECRET_KEY=Secrets.AWS_ACCESS_KEY_ID;
    private DockerClient dockerClient = DockerClientSingleton.getDockerClient();
    public void runContainer(String fileKey,String uploadId)
    {

        CreateContainerResponse container = dockerClient.createContainerCmd(Secrets.DOCKER_TRANSCODER_CONTAINER_IMAGE)

        .withName("transcoding_image")
                .withEnv(
                        List.of(
                                "ACCESS_KEY="+ACCESS_KEY,
                                "SECRET_KEY="+SECRET_KEY,
                                "BUCKET_NAME="+Secrets.AWS_RAW_BUCKET_NAME,
                                "TRANSCODED_BUCKET_NAME="+Secrets.AWS_TRANSCODED_BUCKET_NAME,
                                "FILE_KEY="+fileKey,
                                "UPLOAD_ID="+uploadId,
                                "CLOUDFRONT_URL="+Secrets.CLOUD_FRONT_URL,
                                "UPDATE_API_URL=http://host.docker.internal:8085/api/update"
                        )
                )

                .exec()
                ;
        dockerClient.startContainerCmd(container.getId()).exec();
    }
}
