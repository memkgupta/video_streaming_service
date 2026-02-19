package com.mk.vsnt.moderation_service.config;




import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.google.gson.Gson;
import com.vsnt.common_lib.Secrets;
import com.vsnt.common_lib.dtos.ModerationJob;
import com.vsnt.common_lib.utils.DockerContainerSpawner;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.Date;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    static final String QUEUE_NAME = "moderation_jobs" ;
    private final Gson gson = new Gson();
    private final DockerContainerSpawner spawner;

    public RabbitMQConfig(DockerContainerSpawner spawner) {
        this.spawner = spawner;
    }

    @Bean
    public Queue transcodingQueue() {
        return new Queue(QUEUE_NAME, false);
    }


    @RabbitListener(queues = QUEUE_NAME)
    public void receive(@Payload String data) {

        ModerationJob job = gson.fromJson(data, ModerationJob.class);
        Date expiration = new Date(System.currentTimeMillis() + 100 * 60 * 1000);
        GeneratePresignedUrlRequest request =
                new GeneratePresignedUrlRequest(Secrets.AWS_RAW_BUCKET_NAME, job.getFileKey())
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);
        AmazonS3 client = S3Config.getS3Client();
      String pres =  client.generatePresignedUrl(
request
        ).toString();
        // todo code for generating presigned url
        System.out.println("Received ModerationJob: " + job);
        spawner.spawn(Map.of(
                "PRESIGNED_URL",pres,
                "VIDEO_ID",job.getJobId(),
                "ASSET_SIZE",job.getSize()
        ));

    }
}

