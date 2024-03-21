package net.savantly.mainbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "authorization")
@Data
public class AuthorizationConfig {

    private String adminGroup = "admin";

    
}
