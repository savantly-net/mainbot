package net.savantly.mainbot.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// @Configuration
@ConditionalOnProperty(prefix = "app.security", name = "enabled", havingValue = "true")
@ConfigurationProperties(prefix = "app.websocket.security")
@Data
public class WebSocketSecurityConfig {

    private String[] subscriberDestMatchers = new String[] { "/user/queue/*" };
    private String[] destMatchers = new String[] { "/app/*" };

    @EnableWebSocketSecurity
    class WebSocketSecurityEnabled {
        @Bean
        public AuthorizationManager<Message<?>> messageAuthorizationManager(
                MessageMatcherDelegatingAuthorizationManager.Builder messages) {
            log.info("WebSocketSecurityEnabled");
            return messages
                    .nullDestMatcher().permitAll()
                    .simpSubscribeDestMatchers(subscriberDestMatchers).permitAll()
                    .simpDestMatchers(destMatchers).permitAll()
                    .anyMessage().permitAll().build();

        }
    }

}