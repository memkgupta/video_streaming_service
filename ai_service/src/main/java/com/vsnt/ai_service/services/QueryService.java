package com.vsnt.ai_service.services;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class QueryService {
private final ChatModel chatModel;
private final EmbeddingModel embeddingModel;
private final EmbeddingStore<TextSegment> embeddingStore;
public QueryService(ChatModel chatModel, EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore) {
        this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
    }
public String query(String question , String mediaId)
    {
        Embedding questionEmbedding = embeddingModel.embed(question).content();
        EmbeddingSearchResult<TextSegment> relevantSegments = embeddingStore.search(
               EmbeddingSearchRequest.builder()
                       .filter(new IsEqualTo("mediaId",mediaId))
                       .queryEmbedding(questionEmbedding)
                       .build()
        );
        List<EmbeddingMatch<TextSegment>> matches = relevantSegments.matches();
        String context = matches.stream()
                .map(match -> match.embedded().text())
                .collect(Collectors.joining("\n\n"));

        String promptWithContext = String.format("""
        Answer the question based on the following context.
        If the context doesn't contain relevant information, say "I don't have enough information to answer."

        Context:
        %s

        Question: %s

        Answer:
        """, context, question);
        System.out.println(promptWithContext);
        return chatModel.chat(promptWithContext);
    }
}
