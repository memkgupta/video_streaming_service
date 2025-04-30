package com.vsnt.transcoder.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.vsnt.transcoder.docker_utils.DockerUtils;
import com.vsnt.transcoder.dtos.TranscodingJob;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.Map;

@Configuration
public class RabbitMQConfig {

    static final String QUEUE_NAME = "transcoding_jobs" ;


    @Bean
    public Queue transcodingQueue() {
        return new Queue(QUEUE_NAME, false);
    }


    @RabbitListener(queues = QUEUE_NAME)
    public void receive(@Payload String data) {
        Gson gson = new Gson();
       TranscodingJob job = gson.fromJson(data, TranscodingJob.class);
        System.out.println(job);
        DockerUtils dockerUtils = new DockerUtils();
        dockerUtils.runContainer(job.getKey(),job.getJobId());
    }
}
