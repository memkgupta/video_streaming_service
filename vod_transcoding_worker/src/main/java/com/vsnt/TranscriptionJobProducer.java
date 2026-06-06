package com.vsnt;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.vsnt.common_lib.dtos.jobs.transcription.TranscriptionJob;

import java.util.logging.Logger;

public class TranscriptionJobProducer {
    private final Logger logger = Logger.getLogger(TranscriptionJobProducer.class.getName());
    private final Channel channel;
    private final String queueName;
    private final ObjectMapper objectMapper;
    public TranscriptionJobProducer(Channel channel, String queueName) {
        this.channel = channel;
        this.queueName = queueName;
        this.objectMapper = new ObjectMapper();
    }
    public void publish(TranscriptionJob job) throws Exception {

        byte[] payload =
                objectMapper.writeValueAsBytes(job);

        channel.basicPublish(
                "",
                queueName,
                MessageProperties.PERSISTENT_TEXT_PLAIN,
                payload
        );
        logger.info("Produced TranscriptionJob for asset with assetId "+job.getAssetId());
    }

}
