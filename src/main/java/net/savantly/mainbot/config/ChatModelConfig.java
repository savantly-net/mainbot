package net.savantly.mainbot.config;

import java.time.Duration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.RequiredArgsConstructor;
import net.savantly.mainbot.service.replicate.ReplicateClient;

@Configuration
@RequiredArgsConstructor
public class ChatModelConfig {

    private final OpenAIConfig openAIConfig;

    @Bean
    @Primary
    @ConditionalOnProperty(prefix = "openai", name = "enabled", havingValue = "true")
    public ChatLanguageModel getChatModel(ReplicateClient replicateClient) {
        return getOpenAiChatModel();
        // return new ReplicateChatLanguageModel(replicateClient);
    }

    public ChatLanguageModel getOpenAiChatModel() {
        String apiKey = openAIConfig.getApiKey();

        return OpenAiChatModel.builder()
                .apiKey(apiKey) // https://platform.openai.com/account/api-keys
                .modelName(openAIConfig.getChatModelId())
                .temperature(0.1)
                .logResponses(false)
                .logRequests(false)
                .timeout(Duration.ofSeconds(openAIConfig.getTimeoutSeconds()))
                .build();
    }
}
