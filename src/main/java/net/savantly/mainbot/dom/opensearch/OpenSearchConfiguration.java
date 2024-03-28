package net.savantly.mainbot.dom.opensearch;

import javax.net.ssl.SSLContext;

import org.opensearch.client.RestHighLevelClient;
import org.opensearch.data.client.orhlc.AbstractOpenSearchConfiguration;
import org.opensearch.data.client.orhlc.ClientConfiguration;
import org.opensearch.data.client.orhlc.RestClients;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import lombok.Data;
import net.savantly.mainbot.dom.apiclient.ApiClient;
import net.savantly.mainbot.dom.apiclient.SslContextGenerator;
import net.savantly.mainbot.dom.documents.DocumentService;
import net.savantly.mainbot.dom.documents.processing.DocumentProcessorManager;

@Configuration
@ConfigurationProperties(prefix = "opensearch")
@ConditionalOnProperty(name = "documents.implementation", havingValue = "OPENSEARCH")
@Data
public class OpenSearchConfiguration {

    private String url = "https://localhost:9200";
    private HttpHeaders defaultHeaders = new HttpHeaders();
    private String trustStorePath;
    private String trustStorePassword;
    private String clientCertPath;
    private String clientKeyPath;

    @Bean
    public DocumentService openSearchDocumentService(DocumentProcessorManager processorManager, RestHighLevelClient client) {
        return new OpenSearchDocumentService(this, processorManager, client);
    }

    public class RestClientConfig extends AbstractOpenSearchConfiguration {

        @Override
        @Bean
        public RestHighLevelClient opensearchClient() {

            var sslContextBuilder = new SslContextGenerator.Builder();
            if (trustStorePath != null) {
                sslContextBuilder.withTrustStore(trustStorePath, trustStorePassword, "jks");
            }
            if (clientCertPath != null) {
                sslContextBuilder.withPEMKeys(clientCertPath, clientKeyPath, trustStorePassword);
            }
            SSLContext sslContext = null;
            try {
                sslContext = sslContextBuilder.build();
            } catch (Exception e) {
                throw new RuntimeException("Problem configuring the SSL context", e);
            }

            var esHeaders = new org.springframework.data.elasticsearch.support.HttpHeaders();
            esHeaders.addAll(defaultHeaders);

            final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                    .connectedTo(url)
                    .usingSsl(sslContext)
                    .withDefaultHeaders(esHeaders)
                    .build();

            return RestClients.create(clientConfiguration).rest();
        }
    }

}
