package net.savantly.mainbot.dom.documents;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.savantly.mainbot.dom.embeddingstore.DocumentEmbedder;
import net.savantly.mainbot.dom.embeddingstore.EmbeddingStoreProvider;

@Configuration
public class DocumentsConfiguration {

    @Bean
    public DocumentService documentService(DocumentEmbedder embedder, EmbeddingStoreProvider storeProvider) {
        return new DocumentService(embedder, storeProvider);
    }

}
