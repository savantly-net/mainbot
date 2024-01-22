package net.savantly.mainbot.config;

import static dev.langchain4j.model.openai.OpenAiModelName.GPT_3_5_TURBO_16K_0613;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "openai")
public class OpenAIConfig {

    @Getter
    @Setter
    private String apiKey = "CHANGEME";

    @Getter
    @Setter
    private String chatModelId = GPT_3_5_TURBO_16K_0613;

    @Getter
    @Setter
    private int timeoutSeconds = 30;

    @Getter
    @Setter
    private double temperature = 0.5;

}
