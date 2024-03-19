package net.savantly.mainbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "pinecone")
@Getter @Setter
public class PineconeConfig {
    
    private boolean enabled = true;
    private String apiKey;
    private String environment = "us-east4-gcp";
    private String projectName;
    private String index;

    private String namespacePrefix = "mainbot_";
}
