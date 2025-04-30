package com.vsnt.transcoder.docker_utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.vsnt.transcoder.config.DockerClientSingleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DockerUtils {
//    @Value("${aws.access_key}")
    private String ACCESS_KEY = "SHAITAN_GALI";
//    @Value("${aws.secret_key}")
    private String SECRET_KEY="KHATRA_MAHAL";
    private DockerClient dockerClient = DockerClientSingleton.getDockerClient();
    public void runContainer(String fileKey,String uploadId)
    {
        System.out.println(ACCESS_KEY + ":" + SECRET_KEY);
        CreateContainerResponse container = dockerClient.createContainerCmd("transcoding_image")

        .withName("transcoding_image")
                .withEnv(
                        List.of(
                                "ACCESS_KEY="+ACCESS_KEY,
                                "SECRET_KEY="+SECRET_KEY,
                                "BUCKET_NAME=KOLAPUR",
                                "TRANSCODED_BUCKET_NAME=ANDHER_NAGAR",
                                "FILE_KEY="+fileKey,
                                "UPLOAD_ID="+uploadId,
                                "CLOUDFRONT_URL=GULLU",
                                "UPDATE_API_URL=http://host.docker.internal:8085/api/update"
                        )
                )

                .exec()
                ;
        dockerClient.startContainerCmd(container.getId()).exec();
    }
}
