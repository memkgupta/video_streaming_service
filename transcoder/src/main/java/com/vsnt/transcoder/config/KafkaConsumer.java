package com.vsnt.transcoder.config;

import com.vsnt.transcoder.StreamsTranscodingProcesses;
import com.vsnt.transcoder.dtos.StreamChunk;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KafkaConsumer {
    private final StreamsTranscodingProcesses processes;

    public KafkaConsumer(StreamsTranscodingProcesses processes) {
        this.processes = processes;
    }


    @KafkaListener(topics = "video-stream",groupId = "stream-consumer")
    public void consume(ConsumerRecord<String, byte[]> record) {

        StreamChunk chunk = new StreamChunk();

        chunk.setStreamId(record.key());
        chunk.setData(record.value());

//        System.out.println("Stream: " + chunk.getStreamId());
//        System.out.println("Size: " + chunk.getData().length);
        if(!processes.containsProcess(chunk.getStreamId()))
        {
            processes.spawnProcess(chunk.getStreamId());
        }
        processes.get(chunk.getStreamId()).stdin(
                chunk.getData(), chunk.getStreamId()
        );
    }
}
