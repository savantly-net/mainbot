package net.savantly.mainbot.dom.opensearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.bulk.IndexOperation;

import com.fasterxml.jackson.core.JacksonException;

import lombok.extern.slf4j.Slf4j;
import net.savantly.mainbot.dom.documents.DocumentAddRequest;
import net.savantly.mainbot.dom.documents.DocumentPart;
import net.savantly.mainbot.dom.documents.DocumentQuery;
import net.savantly.mainbot.dom.documents.DocumentSearchResult;
import net.savantly.mainbot.dom.documents.DocumentService;
import net.savantly.mainbot.dom.documents.DocumentPartCollection;
import net.savantly.mainbot.dom.documents.processing.DocumentProcessorManager;
import net.savantly.mainbot.dom.embeddingstore.DocumentEmbedder;

@Slf4j
public class OpenSearchDocumentService implements DocumentService {

    private final DocumentProcessorManager processorManager;
    private final DocumentEmbedder documentEmbedder;
    private final OpenSearchClient client;
    private final OpenSearchConfiguration config;

    public OpenSearchDocumentService(DocumentProcessorManager processorManager,
            DocumentEmbedder documentEmbedder, OpenSearchClient client, OpenSearchConfiguration config) {
        this.processorManager = processorManager;
        this.documentEmbedder = documentEmbedder;
        this.client = client;
        this.config = config;
    }

    @Override
    public List<String> addDocument(DocumentAddRequest documentRequest) {

        final DocumentPartCollection processedDoc = processorManager
                .processDocument(DocumentPartCollection.fromAddRequest(documentRequest));

        // embed the document
        for (var part : processedDoc.getParts()) {
            var embeddingResponse = documentEmbedder.createEmbedding(part.getText());
            log.debug("embedding response: {}", embeddingResponse.tokenUsage());
            part.setEmbedding(embeddingResponse.content().vector());
        }

        // index the documents
        var bulkRequest = BulkRequest.of(fn -> {
            fn.index(config.getBootstrap().getIndexName());
            for (var part : processedDoc.getParts()) {
                fn.operations(operations -> {
                    operations.index(IndexOperation.of(iBuilder -> {
                        return iBuilder
                                .id(part.getId())
                                .document(part);
                    }));
                    return operations;
                });
            }
            return fn;
        });

        try {
            client.bulk(bulkRequest);
        } catch (JacksonException e) {
            log.error("jackson exception. This occurs when the document is not serializable, or maybe there's authentication issues");
            throw new RuntimeException("Problem indexing document", e);
        } catch (OpenSearchException e) {
            throw new RuntimeException("Problem indexing document", e);
        } catch (IOException e) {
            throw new RuntimeException("Problem indexing document", e);
        }

        var docParts = new ArrayList<String>();
        for (var part : processedDoc.getParts()) {
            docParts.add(part.getId());
        }
        return docParts;
    }

    @Override
    public List<DocumentSearchResult> search(DocumentQuery query, String namespace) {
        // Build the query
        log.info("Searching for: {}", query.getText());
        var searchRequest = SearchRequest.of(requestBuilder -> {
            requestBuilder.query(queryBuilder -> {
                queryBuilder.knn(knnBuilder -> {
                    knnBuilder.field("embedding").k(query.getMaxResults()).vector(vectorize(query.getText()));
                    return knnBuilder;
                });
                return queryBuilder;
            });
            return requestBuilder;
        });

        try {
            // Execute the search
            var searchResponse = client.search(searchRequest, DocumentPart.class);
            log.info("Search response: total hits {}", searchResponse.hits().total().value());

            // Extract the results
            // Process the search hits
            var hitsMetadata = searchResponse.hits();
            List<DocumentSearchResult> searchResults = new ArrayList<>();
            for (var hit : hitsMetadata.hits()) {
                DocumentSearchResult result = new DocumentSearchResult();
                result.setEmbeddingId(hit.id());
                result.setScore(hit.score());
                result.setText(hit.source().getText());
                result.setUri(hit.source().getUri());
                result.setMetadata(hit.source().getMetadata());
                searchResults.add(result);
            }
            return searchResults;

        } catch (Exception e) {
            throw new RuntimeException("Problem executing search", e);
        }
    }

    private float[] vectorize(String text) {
        var embeddingResponse = documentEmbedder.createEmbedding(text);
        return embeddingResponse.content().vector();
    }

}
