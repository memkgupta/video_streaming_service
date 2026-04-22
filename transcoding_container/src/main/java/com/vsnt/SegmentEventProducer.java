package com.vsnt;

import com.google.gson.Gson;
import com.vsnt.dtos.TranscodingFinishEventDTO;
import com.vsnt.dtos.TranscodingSegmentUpdateDTO;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;

public class SegmentEventProducer {

    private final KafkaProducer<String, String> producer;
    private final String update_topic;
    private final String finish_topic;
    private final Gson gson = new Gson();

    public SegmentEventProducer(String bootstrapServers, String update_topic , String finish_topic)  {

        this.finish_topic = finish_topic;
this.update_topic = update_topic;
        Properties props = new Properties();
        System.out.println(bootstrapServers);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka_q4:9092");
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

    public void sendEvent(TranscodingSegmentUpdateDTO event) {

        String key = event.getAssetId(); // ensures ordering per asset
        String value = gson.toJson(event);

        ProducerRecord<String, String> record =
                new ProducerRecord<>(update_topic, key, value);

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
    public void close() {
        producer.flush();
        producer.close();
    }
}
