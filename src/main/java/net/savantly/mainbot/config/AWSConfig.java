package net.savantly.mainbot.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@ConditionalOnProperty(prefix = "aws", name = "enabled", havingValue = "true")
public class AWSConfig {

    @Bean
    public S3Client s3Client() {
        return S3Client.create();
    }
}
