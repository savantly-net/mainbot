package net.savantly.mainbot.dom.seed;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "seed")
public class SeedConfigProperties {
    
    private boolean enabled = true;
    private String seedFolder = "classpath:seed";
    private String namespace = "/admin";
}
