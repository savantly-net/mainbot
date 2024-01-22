package net.savantly.mainbot.service.languagetools;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import net.savantly.mainbot.config.OpenAIConfig;

@Configuration
@RequiredArgsConstructor
public class LanguageToolsConfiguration {

    private final OpenAIConfig openAIConfig;

    @Value("${language-tools.log-requests:false}")
    private boolean logRequests = false;
    @Value("${language-tools.log-responses:true}")
    private boolean logResponses = true;

    @Bean
    @ConditionalOnProperty(prefix = "openai", name = "enabled", havingValue = "true")
    public LanguageToolModel getLanguageToolModel() {
        String apiKey = openAIConfig.getApiKey();

        var chatModel = OpenAiChatModel.builder()
                .apiKey(apiKey) // https://platform.openai.com/account/api-keys
                .modelName(openAIConfig.getChatModelId())
                .temperature(openAIConfig.getTemperature())
                .logResponses(logResponses)
                .logRequests(logRequests)
                .timeout(Duration.ofSeconds(openAIConfig.getTimeoutSeconds()))
                .build();
        return new LanguageToolModel() {

            @Override
            public Response<AiMessage> generate(List<ChatMessage> messages) {
                return chatModel.generate(messages);
            }

            @Override
            public Response<AiMessage> generate(List<ChatMessage> messages,
                    List<ToolSpecification> toolSpecifications) {
                return chatModel.generate(messages, toolSpecifications);
            }

            @Override
            public Response<AiMessage> generate(List<ChatMessage> messages, ToolSpecification toolSpecification) {
                return chatModel.generate(messages, toolSpecification);
            }

        };
    }

    @Bean
    @ConditionalOnProperty(prefix = "openai", name = "enabled", havingValue = "false", matchIfMissing = true)
    public LanguageToolModel getMockLanguageToolModel() {
        return new MockChatLanguageModel();
    }

    @Bean
    public LanguageTools getLanguageTools(LanguageToolModel model) {
        return AiServices.builder(LanguageTools.class)
                .chatLanguageModel(model)
                .build();
    }
}
