package net.savantly.mainbot.dom.opensearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.savantly.mainbot.dom.documents.DocumentService;
import net.savantly.mainbot.dom.documents.processing.DocumentProcessorManager;
import net.savantly.mainbot.dom.embeddingstore.DocumentEmbedder;

@Configuration
@ConfigurationProperties(prefix = "opensearch")
@ConditionalOnProperty(name = "documents.implementation", havingValue = "OPENSEARCH")
@Data
@Slf4j
public class OpenSearchConfiguration {

    private String url = "https://localhost:9200";
    private String pathPrefix = "/";
    private HttpHeaders defaultHeaders = new HttpHeaders();

    /**
     * The headers to forward from the incoming request to the opensearch cluster.
     */
    private List<String> forwardedHeaders = new ArrayList<>();

    private String trustStorePath;
    private String trustStorePassword;
    private String clientCertPath;
    private String clientKeyPath;

    private boolean skipHostnameVerification = false;
    private boolean useInsecureSSL = false;

    private Bootstrap bootstrap = new Bootstrap();

    /**
     * The global authentication configuration for the opensearch cluster. Will be
     * used for every user request.
     */
    private AuthenticationConfig globalAuthentication = new AuthenticationConfig();

    @PostConstruct
    public void init() {
        log.info("opensearch configuration: {}", this);
    }

    @Bean
    public OpenSearchClient globalOpenSearchClient(OAuth2AuthorizedClientManager clientManager) {
        var restClientConfig = new RestClientConfig(this, this.globalAuthentication, clientManager);
        return restClientConfig.opensearchClient();
    }

    @Bean
    public DocumentService openSearchDocumentService(DocumentProcessorManager processorManager,
            DocumentEmbedder embedder, OpenSearchClient client, OAuth2AuthorizedClientManager clientManager)
            throws OpenSearchException, IOException {

        // run the bootstrapper first. be sure to use the bootstrapper client
        var bootStrapper = new OpenSearchBoostrapper(bootstrapperOpenSearchClient(clientManager), this.bootstrap);
        bootStrapper.bootstrap();

        return new OpenSearchDocumentService(processorManager, embedder, client, this);
    }

    private OpenSearchClient bootstrapperOpenSearchClient(OAuth2AuthorizedClientManager clientManager) {
        var restClientConfig = new RestClientConfig(this, this.bootstrap.getAuthentication(), clientManager);
        return restClientConfig.opensearchClient();
    }

    static enum AuthenticationMethod {
        NONE, BASIC, OAUTH2
    }

    @Data
    static class AuthenticationConfig {
        /**
         * Use this to specify the authentication method to use when connecting to the
         * opensearch cluster.
         * <p>
         * Valid values are: NONE, BASIC, OAUTH2
         */
        private AuthenticationMethod method = AuthenticationMethod.NONE;

        private OAuth2 oauth2 = new OAuth2();
        private BasicAuth basic = new BasicAuth();
    }

    @Data
    static class OAuth2 {

        /**
         * Use this to specify the client registration id for the oauth2 client that is
         * used to authenticate with the opensearch cluster.
         * Only required if the opensearch cluster is secured with oauth2.
         */
        private String clientRegistrationId;
    }

    @Data
    static class BasicAuth {
        private String username = "admin";
        private String password = "admin";
    }

    @Data
    static class Bootstrap {
        private boolean enabled = true;
        private String indexName = "mainbot-documents";

        private AuthenticationConfig authentication = new AuthenticationConfig();

        // Machine Learning plugin configuration
        private boolean enableMachineLearning = false;
        private String modelGroupName = "mainbot-model-group";
        private String modelName = "mainbot-model";
        private String modelRegistrationPath = "classpath:/opensearch/models/openai.json";
        private Map<String, String> modelRegistrationReplacements = new HashMap<>();

        // NOT USING YET
        private String agentWorkflowPath = "classpath:/opensearch/workflows/agent_workflow.yml";
        /**
         * The workflow parameters to use when deploying the workflow.
         * The key is the parameter name, and the value is the parameter value.
         */
        private Map<String, String> agentWorkflowReplacements = new HashMap<>();
    }
}
