package net.savantly.mainbot.dom.documents;

import java.util.List;

import dev.langchain4j.data.document.Document;
import lombok.RequiredArgsConstructor;
import net.savantly.mainbot.dom.embeddingstore.DocumentEmbedder;
import net.savantly.mainbot.dom.embeddingstore.EmbeddingStoreProvider;

@RequiredArgsConstructor
public class DocumentService {
    
    private final DocumentEmbedder documentEmbedder;
    private final EmbeddingStoreProvider embeddingStoreProvider;

    public List<String> addDocument(Document document, String namespace) {
        var store = embeddingStoreProvider.embeddingStore(namespace);
        return documentEmbedder.addDocumentsToEmbeddingStore(store, document);
    }

    public List<DocumentSearchResult> search(DocumentQuery query, String namespace) {
        var store = embeddingStoreProvider.embeddingStore(namespace);
        var referenceEmbedding = documentEmbedder.createEmbedding(query.getText()).content();
        var matches = store.findRelevant(referenceEmbedding, query.getMaxResults(), query.getMinScore());
        var results = matches.stream()
            .map(match -> new DocumentSearchResult()
                .setEmbeddingId(match.embeddingId())
                .setText(match.embedded().text())
                .setScore(match.score())
                .setMetadata(match.embedded().metadata().asMap())
            )
            .toList();

        return results;
    }
}
