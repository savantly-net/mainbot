package net.savantly.mainbot.dom.opensearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.opensearch.client.RestHighLevelClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import net.savantly.mainbot.dom.apiclient.ApiClient;
import net.savantly.mainbot.dom.documents.DocumentAddRequest;
import net.savantly.mainbot.dom.documents.DocumentQuery;
import net.savantly.mainbot.dom.documents.DocumentSearchResult;
import net.savantly.mainbot.dom.documents.DocumentService;
import net.savantly.mainbot.dom.documents.IndexDocument;
import net.savantly.mainbot.dom.documents.processing.DocumentProcessorManager;
import net.savantly.mainbot.dom.embeddingstore.DocumentEmbedder;
import net.savantly.mainbot.dom.embeddingstore.EmbeddingStoreProvider;

@Slf4j
public class OpenSearchDocumentService implements DocumentService {

    final private OpenSearchConfiguration configuration;
    final private DocumentProcessorManager processorManager;
    final private RestTemplate restTemplate;
    private final DocumentEmbedder documentEmbedder;
    RestHighLevelClient client;

    public OpenSearchDocumentService(OpenSearchConfiguration configuration, DocumentProcessorManager processorManager, DocumentEmbedder documentEmbedder, RestHighLevelClient client) {
        this.configuration = configuration;
        this.processorManager = processorManager;
        this.documentEmbedder = documentEmbedder;
        this.client = client;

        HttpHeaders defaultHeaders = configuration.getDefaultHeaders();
        log.debug("OpenSearchDocumentService created with default headers: {}", defaultHeaders);

        // Add a custom interceptor to add headers to each request
        ClientHttpRequestInterceptor headerInterceptor = (request, body, execution) -> {
            request.getHeaders().addAll(defaultHeaders);
            return execution.execute(request, body);
        };

        var restTemplateBuilder = new ApiClient.Builder();

        if (Objects.nonNull(configuration.getTrustStorePath())) {
            try {
                String trustStorePath = configuration.getTrustStorePath();
                String trustStorePassword = configuration.getTrustStorePassword();
                String trustStoreType = "jks";
                restTemplateBuilder.withTrustStore(trustStorePath, trustStorePassword, trustStoreType);
            } catch (Exception e) {
                log.error("Problem configuring the truststore", e);
                throw new RuntimeException("Problem configuring the truststore", e);
            }
        }

        if (Objects.nonNull(configuration.getClientCertPath())) {
            log.debug("Client cert configured");
            final String clientCertPath = configuration.getClientCertPath();
            final String clientKeyPath = configuration.getClientKeyPath();

            try {
                restTemplateBuilder.withPEMKeys(clientCertPath, clientKeyPath, UUID.randomUUID().toString());
            } catch (Exception e) {
                log.error("Problem configuring the client cert", e);
                throw new RuntimeException("Problem configuring the client cert", e);
            }
        }

        try {
            this.restTemplate = restTemplateBuilder.build();
            this.restTemplate.setInterceptors(Collections.singletonList(headerInterceptor));
        } catch (Exception e) {
            log.error("Problem creating the rest template", e);
            throw new RuntimeException("Problem creating the rest template", e);
        }
    }

    @Override
    public List<String> addDocument(DocumentAddRequest documentRequest) {


        final IndexDocument processedDoc = processorManager
                .processDocument(IndexDocument.fromAddRequest(documentRequest));

        // embed the document
        for (var part : processedDoc.getParts()) {
            var embeddingResponse = documentEmbedder.createEmbedding(part.getText());
            log.debug("embedding response: {}", embeddingResponse);
            part.setEmbedding(embeddingResponse.content().vector());
        }

        String url = getDocumentUrl(documentRequest.getNamespace(), documentRequest.getId());

        HttpHeaders headers = new HttpHeaders();

        HttpEntity<IndexDocument> entity = new HttpEntity<>(processedDoc, headers);

        this.restTemplate.put(url, entity);
        return List.of(processedDoc.getId());
    }

    @Override
    public List<DocumentSearchResult> search(DocumentQuery query, String namespace) {
        // query the opensearch endpoint
        var result = this.restTemplate.getForObject(getSearchUrl(namespace, query.getText()), HashMap.class);
        return extractHits(result);
    }

    private List<DocumentSearchResult> extractHits(Map<String, Object> response) {
        var results = new ArrayList<DocumentSearchResult>();

        var hitsWrapperObject = response.get("hits");
        if (Objects.isNull(hitsWrapperObject)) {
            return results;
        }
        var hitsWrapper = (Map<String, Object>) hitsWrapperObject;

        var hitsObject = hitsWrapper.get("hits");
        if (Objects.isNull(hitsObject)) {
            return results;
        }
        var hits = (List<Map<String, Object>>) hitsWrapper.get("hits");

        for (Map<String, Object> hit : hits) {
            var result = new DocumentSearchResult();
            result.setEmbeddingId((String) hit.get("_id"));
            result.setScore((Double) hit.get("_score"));

            var source = (Map<String, Object>) hit.get("_source");
            if (Objects.nonNull(source)) {
                result.setText((String) source.get("text"));
                result.setUri((String) source.get("uri"));
            }
            results.add(result);
        }

        return results;
    }

    private String getDocumentUrl(String namespace, String id) {
        return configuration.getUrl() + "/" + namespace + "/_doc/" + id;
    }

    private String getSearchUrl(String namespace, String query) {
        return configuration.getUrl() + "/" + namespace + "/_search?q=" + query;
    }

}
