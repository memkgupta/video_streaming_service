package com.mk.vsnt.moderation_service.config;




import com.google.gson.Gson;
import com.vsnt.common_lib.dtos.ModerationJob;
import com.vsnt.common_lib.utils.DockerContainerSpawner;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Payload;

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

        // todo code for generating presigned url

        spawner.spawn(Map.of(
                "PRESIGNED_URL",job.getFileKey(),
                "JOB_ID",job.getJobId(),
                "ASSET_SIZE",job.getSize()
        ));

    }
}

