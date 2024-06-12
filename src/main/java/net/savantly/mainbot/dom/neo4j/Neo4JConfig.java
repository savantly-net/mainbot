package net.savantly.mainbot.dom.neo4j;

import org.neo4j.cypherdsl.core.renderer.Dialect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.savantly.mainbot.dom.documents.DocumentService;
import net.savantly.mainbot.dom.documents.impl.LC4JDocumentService;
import net.savantly.mainbot.dom.documents.processing.DocumentProcessorManager;
import net.savantly.mainbot.dom.embeddingstore.DocumentEmbedder;
import net.savantly.mainbot.dom.embeddingstore.EmbeddingStoreProvider;
import net.savantly.mainbot.dom.neo4j.dto.Neo4JEntityPersistor;
import net.savantly.mainbot.dom.neo4j.entity.PersonRepository;
import net.savantly.mainbot.dom.neo4j.entity.PlaceRepository;
import net.savantly.mainbot.dom.neo4j.entity.ThingRepository;

@Configuration
@ConfigurationProperties(prefix = "neo4j")
@ConditionalOnProperty(name = "documents.implementation", havingValue = "NEO4J")
public class Neo4JConfig {

    @Bean
    org.neo4j.cypherdsl.core.renderer.Configuration cypherDslConfiguration() {
        return org.neo4j.cypherdsl.core.renderer.Configuration.newConfig()
                .withDialect(Dialect.NEO4J_5).build();
    }

    @Bean
    @ConditionalOnMissingBean(Neo4JEntityExtractor.class)
    public Neo4JEntityExtractor neo4JEntityExtractor() {
        return new Neo4JEntityExtractor() {
        };
    }

    @Bean
    @ConditionalOnMissingBean(Neo4JEntityPersistor.class)
    public Neo4JEntityPersistor neo4JEntityPersistor(PersonRepository personRepository, PlaceRepository placeRepository,
            ThingRepository thingRepository) {
        return new Neo4JEntityPersistor(personRepository, placeRepository, thingRepository);
    }

    @Bean
    public DocumentService documentService(
            Neo4JEntityExtractor entityExtractor,
            Neo4JEntityPersistor entityPersistor,
            DocumentEmbedder embedder,
            EmbeddingStoreProvider storeProvider,
            DocumentProcessorManager processorManager) {

        var lc4jDocumentService = new LC4JDocumentService(embedder, storeProvider, processorManager);

        return new Neo4JDocumentService(entityExtractor, entityPersistor, lc4jDocumentService);
    }

}
