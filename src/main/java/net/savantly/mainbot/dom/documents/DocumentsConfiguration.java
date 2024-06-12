package net.savantly.mainbot.dom.documents;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import net.savantly.mainbot.dom.documents.chunking.ChunkingConfiguration;
import net.savantly.mainbot.dom.documents.chunking.DocumentChunker;
import net.savantly.mainbot.dom.documents.impl.LC4JDocumentService;
import net.savantly.mainbot.dom.documents.processing.DocumentProcessor;
import net.savantly.mainbot.dom.documents.processing.DocumentProcessorManager;
import net.savantly.mainbot.dom.embeddingstore.DocumentEmbedder;
import net.savantly.mainbot.dom.embeddingstore.EmbeddingStoreProvider;

@Configuration
@ConfigurationProperties(prefix = "documents")
@Data
public class DocumentsConfiguration {

    private DocumentServiceImplementationType implementation = DocumentServiceImplementationType.LC4J;

    @Bean
    public DocumentProcessorManager documentProcessorManager(final List<DocumentProcessor> processors) {
        return new DocumentProcessorManager(processors);
    }

    @Bean
    @ConditionalOnProperty(name = "documents.implementation", havingValue = "LC4J", matchIfMissing = true)
    public DocumentService documentService(
            DocumentEmbedder embedder,
            EmbeddingStoreProvider storeProvider,
            DocumentProcessorManager processorManager) {
        return new LC4JDocumentService(embedder, storeProvider, processorManager);
    }


    @Bean(name = "documentChunker")
    public DocumentProcessor documentChunker(final ChunkingConfiguration config) {
        return new DocumentChunker(config);
    }

    @Bean
    @ConfigurationProperties(prefix = "documents.chunking")
    public ChunkingConfiguration chunkingConfiguration() {
        return new ChunkingConfiguration();
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    static enum DocumentServiceImplementationType {
        LC4J,
        OPENSEARCH,
        NEO4J
    }

}
