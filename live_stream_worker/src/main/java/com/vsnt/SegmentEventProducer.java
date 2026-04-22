package com.vsnt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vsnt.common_lib.dtos.events.asset.transcoding.AssetTranscodingCompletedEvent;
import com.vsnt.common_lib.dtos.events.asset.transcoding.AssetTranscodingFailureEvent;
import com.vsnt.common_lib.dtos.events.asset.transcoding.AssetTranscodingProgressEvent;
import com.vsnt.config.InstantAdapter;
import com.vsnt.config.Secrets;
import com.vsnt.dtos.TranscodingFailedDTO;
import com.vsnt.dtos.TranscodingFinishEventDTO;
import com.vsnt.dtos.TranscodingSegmentUpdateDTO;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Instant;
import java.util.Properties;

public class SegmentEventProducer {

    private final KafkaProducer<String, String> producer;
    private final String update_topic;
    private final String finish_topic;
    private final String fail_topic;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();

    public SegmentEventProducer(String bootstrapServers, String update_topic , String finish_topic, String failTopic)  {

        this.finish_topic = finish_topic;
this.update_topic = update_topic;
        fail_topic = failTopic;
        Properties props = new Properties();
        System.out.println(bootstrapServers);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Secrets.KAFKA_BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");

        // Production-safe configs
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        System.out.println(props);
        this.producer = new KafkaProducer<>(props);
    }

    public boolean sendEvent(TranscodingSegmentUpdateDTO event) {

        String key = event.getAssetId(); // ensures ordering per asset
        String value = gson.toJson(event);

        ProducerRecord<String, String> record =
                new ProducerRecord<>(update_topic, key, value);
        boolean[] result = new boolean[1];
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                System.err.println("Failed to send event: " + exception.getMessage());
           result[0] = false;
            } else {
                System.out.println("Event sent to topic " + metadata.topic()
                        + " partition " + metadata.partition()
                        + " offset " + metadata.offset());
                result[0] = true;
            }

        });
        return result[0];
    }
    public boolean sendProgress(AssetTranscodingProgressEvent event)
    {
        String key = event.getAssetId(); // ensures ordering per asset
        String value = gson.toJson(event);

        ProducerRecord<String, String> record =
                new ProducerRecord<>("asset-updates", key, value);
        boolean[] result = new boolean[1];
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                System.err.println("Failed to send event: " + exception.getMessage());
                result[0] = false;
            } else {
                System.out.println("Event sent to topic " + metadata.topic()
                        + " partition " + metadata.partition()
                        + " offset " + metadata.offset());
                result[0] = true;
            }

        });
        return result[0];
    }
    public void sendFinishEvent(TranscodingFinishEventDTO event)
    {
        String key = event.getMediaId(); // ensures ordering per asset
        String value = gson.toJson(event);

        ProducerRecord<String, String> record =
                new ProducerRecord<>(finish_topic, key, value);

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                System.err.println("Failed to send event: " + exception.getMessage());
            } else {
                System.out.println("Event sent to topic " + metadata.topic()
                        + " partition " + metadata.partition()
                        + " offset " + metadata.offset());
            }
        });
    }
    public void sendFinishEvent(AssetTranscodingCompletedEvent event)
    {
        String key = event.getAssetId();
        String value = gson.toJson(event);

        ProducerRecord<String, String> record =
                new ProducerRecord<>("asset-updates", key, value);

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                System.err.println("Failed to send event: " + exception.getMessage());
            } else {
                System.out.println("Event sent to topic " + metadata.topic()
                        + " partition " + metadata.partition()
                        + " offset " + metadata.offset());
            }
        });
    }
    public void sendFailedEvent(TranscodingFailedDTO event)
    {
        String key = event.getMediaId(); // ensures ordering per asset
        String value = gson.toJson(event);

        ProducerRecord<String, String> record =
                new ProducerRecord<>(finish_topic, key, value);
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                System.err.println("Failed to send event: " + exception.getMessage());
            } else {
                System.out.println("Event sent to topic " + metadata.topic()
                        + " partition " + metadata.partition()
                        + " offset " + metadata.offset());
            }
        });
    }
    public void sendFailedEvent(AssetTranscodingFailureEvent event)
    {
        String key = event.getAssetId();
        String value = gson.toJson(event);
        ProducerRecord<String,String> record =
                new ProducerRecord<>("asset-updates", key, value);
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                System.err.println("Failed to send event: " + exception.getMessage());
            } else {
                System.out.println("Event sent to topic " + metadata.topic()
                        + " partition " + metadata.partition()
                        + " offset " + metadata.offset());
            }
        });
    }
    public void close() {
        producer.flush();
        producer.close();
    }
}
