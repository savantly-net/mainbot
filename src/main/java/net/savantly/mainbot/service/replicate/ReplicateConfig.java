package net.savantly.mainbot.service.replicate;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "replicate")
@Data
public class ReplicateConfig {
    
    private String url = "https://api.replicate.com";
    private String version = "v1";
    private String apiToken;

    @Bean
    public ReplicateClient replicateClient(RestTemplateBuilder restTemplateBuilder) {
        return new ReplicateClient(this, restTemplateBuilder);
    }
}
