package net.savantly.mainbot.logging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggerAspectConfig {
    
    @Bean
    public LoggerAspect appLoggerAspect() {
        return new LoggerAspect();
    }
}
