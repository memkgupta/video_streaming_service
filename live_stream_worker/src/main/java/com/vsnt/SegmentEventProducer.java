package com.vsnt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vsnt.common_lib.dtos.events.asset.transcoding.*;
import com.vsnt.config.InstantAdapter;
import com.vsnt.config.Secrets;
import com.vsnt.dtos.*;
import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.Future;

public class SegmentEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(SegmentEventProducer.class);

    private final KafkaProducer<String, String> producer;
    private final String updateTopic;
    private final String finishTopic;
    private final String failTopic;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();

    public SegmentEventProducer(String bootstrapServers,
                                String updateTopic,
                                String finishTopic,
                                String failTopic) {

        this.updateTopic = updateTopic;
        this.finishTopic = finishTopic;
        this.failTopic = failTopic;

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Secrets.KAFKA_BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");

        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");

        this.producer = new KafkaProducer<>(props);

        logger.info("Kafka producer initialized. brokers={}, updateTopic={}",
                Secrets.KAFKA_BOOTSTRAP_SERVERS, updateTopic);
    }


    public void sendEvent(TranscodingSegmentUpdateDTO event) {

        String key = event.getAssetId();
        String value = gson.toJson(event);

        ProducerRecord<String, String> record =
                new ProducerRecord<>(updateTopic, key, value);

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                logger.error("Failed to send segment event. assetId={}", key, exception);
            } else {
                logger.debug("Segment event sent. topic={}, partition={}, offset={}, assetId={}",
                        metadata.topic(), metadata.partition(), metadata.offset(), key);
            }
        });
    }

    public void sendProgress(AssetTranscodingProgressEvent event) {

        String key = event.getAssetId();
        String value = gson.toJson(event);

        ProducerRecord<String, String> record =
                new ProducerRecord<>(updateTopic, key, value);

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                logger.error("Failed to send progress event. assetId={}", key, exception);
            } else {
                logger.debug("Progress event sent. assetId={}, offset={}",
                        key, metadata.offset());
            }
        });
    }

    public void sendFinishEvent(TranscodingFinishEventDTO event) {

        String key = event.getMediaId();
        String value = gson.toJson(event);

        ProducerRecord<String, String> record =
                new ProducerRecord<>(finishTopic, key, value);

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                logger.error("Failed to send finish event. mediaId={}", key, exception);
            } else {
                logger.info("Finish event sent. mediaId={}, offset={}", key, metadata.offset());
            }
        });
    }

    public void sendFinishEvent(AssetTranscodingCompletedEvent event) {

        String key = event.getAssetId();
        String value = gson.toJson(event);

        ProducerRecord<String, String> record =
                new ProducerRecord<>(updateTopic, key, value);

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                logger.error("Failed to send completed event. assetId={}", key, exception);
            } else {
                logger.info("Completed event sent. assetId={}, offset={}", key, metadata.offset());
            }
        });
    }

    public void sendFailedEvent(TranscodingFailedDTO event) {

        String key = event.getMediaId();
        String value = gson.toJson(event);

        ProducerRecord<String, String> record =
                new ProducerRecord<>(failTopic, key, value);

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                logger.error("Failed to send failed event. mediaId={}", key, exception);
            } else {
                logger.warn("Failure event sent. mediaId={}, offset={}", key, metadata.offset());
            }
        });
    }

    public void sendFailedEvent(AssetTranscodingFailureEvent event) {

        String key = event.getAssetId();
        String value = gson.toJson(event);

        ProducerRecord<String, String> record =
                new ProducerRecord<>(updateTopic, key, value);

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                logger.error("Failed to send failure event. assetId={}", key, exception);
            } else {
                logger.warn("Failure event sent. assetId={}, offset={}", key, metadata.offset());
            }
        });
    }

    public void close() {
        logger.info("Closing Kafka producer...");
        producer.flush();
        producer.close();
    }
}