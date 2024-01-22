package net.savantly.mainbot.dom.embeddingstore;

import java.util.List;

import org.springframework.stereotype.Service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DocumentEmbedder {
    
    private final EmbeddingModel embeddingModel;

    /**
     * Embeds the given document and stores the embeddings in the given embedding store
     * @param embeddingStore
     * @param document
     */
    public List<String> addDocumentsToEmbeddingStore(EmbeddingStore<TextSegment> embeddingStore, Document document) {
        // Split document into segments (one paragraph per segment)
        DocumentSplitter splitter = new DocumentByParagraphSplitter(500, 50);
        List<TextSegment> documentSegments = splitter.split(document);

        // Embed segments (convert them into semantic vectors)
        Response<List<Embedding>> embeddings = embeddingModel.embedAll(documentSegments);

        // Store embeddings into embedding store for further search / retrieval
        return embeddingStore.addAll(embeddings.content(), documentSegments);
    }

    /**
     * Embeds the given text
     * @param text
     * @return
     */
    public Response<Embedding> createEmbedding(String text) {
        return embeddingModel.embed(text);
    }
}
