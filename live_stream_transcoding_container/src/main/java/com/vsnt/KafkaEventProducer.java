package com.vsnt;



import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;
public class KafkaEventProducer {

    private final KafkaProducer<String, String> producer;

    private final ObjectMapper objectMapper;

    public KafkaEventProducer() {

        Properties props = new Properties();

        props.put("bootstrap.servers", AppConfig.KAFKA_BOOTSTRAP);

        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer<>(props);

        objectMapper = new ObjectMapper();
    }

    public void send(TranscodingSegmentUpdateDTO dto) {

        try {

            String json =
                    objectMapper.writeValueAsString(dto);

            ProducerRecord<String, String> record =
                    new ProducerRecord<>(
                            AppConfig.KAFKA_TOPIC,
                            dto.getMediaId(),
                            json
                    );

            producer.send(record);

            System.out.println("Kafka event sent: " + json);

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }
}
