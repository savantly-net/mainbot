package net.savantly.mainbot.dom.documents.impl;

import java.util.ArrayList;
import java.util.List;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import lombok.RequiredArgsConstructor;
import net.savantly.mainbot.dom.documents.DocumentAddRequest;
import net.savantly.mainbot.dom.documents.DocumentPart;
import net.savantly.mainbot.dom.documents.DocumentQuery;
import net.savantly.mainbot.dom.documents.DocumentSearchResult;
import net.savantly.mainbot.dom.documents.DocumentService;
import net.savantly.mainbot.dom.documents.DocumentPartCollection;
import net.savantly.mainbot.dom.documents.processing.DocumentProcessorManager;
import net.savantly.mainbot.dom.embeddingstore.DocumentEmbedder;
import net.savantly.mainbot.dom.embeddingstore.EmbeddingStoreProvider;

@RequiredArgsConstructor
public class LC4JDocumentService implements DocumentService {

    private final DocumentEmbedder documentEmbedder;
    private final EmbeddingStoreProvider embeddingStoreProvider;
    private final DocumentProcessorManager documentProcessorManager;

    public List<String> addDocument(DocumentAddRequest document) {

        var store = embeddingStoreProvider.embeddingStore(document.getNamespace());

        final DocumentPartCollection processedDoc = documentProcessorManager
                .processDocument(DocumentPartCollection.fromAddRequest(document));

        final List<String> ids = new ArrayList<>();
        for (DocumentPart part : processedDoc.getParts()) {

            var doc = Document.from(part.getText(), Metadata.from(part.getMetadata()));
            doc.metadata().add("uri", document.getUri());
            var partIds = documentEmbedder.addDocumentsToEmbeddingStore(store, doc);
            ids.addAll(partIds);
        }

        return ids;
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
                        .setUri(match.embedded().metadata().get("uri"))
                        .setMetadata(match.embedded().metadata().asMap()))
                .toList();

        return results;
    }
}
