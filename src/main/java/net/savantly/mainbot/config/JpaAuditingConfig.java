package net.savantly.mainbot.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import lombok.RequiredArgsConstructor;
import net.savantly.mainbot.identity.UserContext;

@RequiredArgsConstructor
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

    private final UserContext userContext;
    
    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAware<String>() {

            @Override
            public Optional<String> getCurrentAuditor() {
                return userContext.getCurrentUserUID();
            }
            
        };
    }
}
