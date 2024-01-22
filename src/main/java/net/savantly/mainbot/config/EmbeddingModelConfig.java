package net.savantly.mainbot.config;

import static dev.langchain4j.model.openai.OpenAiModelName.TEXT_EMBEDDING_ADA_002;
import static java.time.Duration.ofSeconds;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class EmbeddingModelConfig {

    private final OpenAIConfig openAIConfig;

    @Bean
    public EmbeddingModel embeddingModel() {
        EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(openAIConfig.getApiKey()) // https://platform.openai.com/account/api-keys
                .modelName(TEXT_EMBEDDING_ADA_002)
                .timeout(ofSeconds(30))
                .build();
        return embeddingModel;
    }
}
