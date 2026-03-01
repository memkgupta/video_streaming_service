package com.vsnt.transcoder.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.vsnt.transcoder.CapacityReachedException;
import com.vsnt.transcoder.docker_utils.DockerUtils;
import com.vsnt.transcoder.docker_utils.JobAssigner;
import com.vsnt.transcoder.dtos.ModerationStatus;
import com.vsnt.transcoder.dtos.TranscodingJob;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Payload;

import java.io.IOException;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    static final String QUEUE_NAME = "transcoding_jobs" ;
    private final JobAssigner jobAssigner;
    public RabbitMQConfig(JobAssigner jobAssigner) {
        this.jobAssigner = jobAssigner;
    }
    @Bean
    public Queue transcodingQueue() {
        return new Queue(QUEUE_NAME, false);
    }
    @RabbitListener(queues = QUEUE_NAME)
    public void receive(Message message, Channel channel) throws IOException {

        Gson gson = new Gson();
        TranscodingJob job = gson.fromJson(
                new String(message.getBody()),
                TranscodingJob.class
        );

        long tag = message.getMessageProperties().getDeliveryTag();

        try {
            jobAssigner.assignJob(job);
            // SUCCESS → ACK
            channel.basicAck(tag, false);

        } catch (CapacityReachedException e) {
            // TEMPORARY → Requeue
            channel.basicNack(tag, false, true);

        } catch (Exception e) {
            // PERMANENT FAILURE → Send to DLQ
            channel.basicNack(tag, false, false);
        }
    }
}
