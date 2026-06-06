package com.vsnt.ai_service.config;

import com.vsnt.common_lib.dtos.events.transcription.TranscriptEvent;
import dev.langchain4j.data.document.DefaultDocument;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KafkaConsumer {
    private final EmbeddingStoreIngestor ingestor;

    public KafkaConsumer(EmbeddingStoreIngestor ingestor) {
        this.ingestor = ingestor;
    }
@KafkaListener(topics = "transcript-event",groupId = "ai-service" , containerFactory = "transcriptEventContainerFactory")
    public void consume(TranscriptEvent event)
    {
    try {
        if(event.transcript().isEmpty()) return;
        Document document = new DefaultDocument(event.transcript(),
                Metadata.from(
                        Map.of(

                                "mediaId",event.mediaId(),
                                "chunkNumber",event.chunkNumber()

                        )
                )
        );
        ingestor.ingest(document);
    }
    catch (Exception e) {
        System.out.println(event);
        e.printStackTrace();
    }
    }
}
