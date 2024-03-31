package net.savantly.mainbot.security.oauth2;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.oauth2.client.ClientsConfiguredCondition;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

import lombok.extern.slf4j.Slf4j;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({ OAuth2ClientProperties.class })
@Conditional({ ClientsConfiguredCondition.class })
@Slf4j
public class OAuth2ClientConfiguration {

    /**
     * Not using yet
     */
    // @Bean("authorizedClientManager")
    // public OAuth2AuthorizedClientManager authorizedClientManager(
    //         OAuth2AuthorizedClientProvider authorizedClientProvider,
    //         ClientRegistrationRepository clientRegistrationRepository,
    //         OAuth2AuthorizedClientRepository authorizedClientRepository) {
    //     log.info("Creating OAuth2AuthorizedClientManager");
    //     DefaultOAuth2AuthorizedClientManager authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(
    //             clientRegistrationRepository, authorizedClientRepository);
    //     authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

    //     return authorizedClientManager;
    // }

    @Bean
    public OAuth2AuthorizedClientProvider authorizedClientProvider() {
        log.info("Creating OAuth2AuthorizedClientProvider");
        return OAuth2AuthorizedClientProviderBuilder.builder()
                .authorizationCode()
                .clientCredentials()
                .refreshToken()
                .build();
    }

    @Bean("authorizedClientServiceManager")
    public AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceManager(
            OAuth2AuthorizedClientProvider authorizedClientProvider,
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {
        log.info("Creating AuthorizedClientServiceOAuth2AuthorizedClientManager");
        var authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

    @Bean
    @ConditionalOnMissingBean({ ClientRegistrationRepository.class })
    public ClientRegistrationRepository clientRegistrationRepository(OAuth2ClientProperties properties) {
        log.info("Creating InMemoryClientRegistrationRepository");
        var propertyMapper = new OAuth2ClientPropertiesMapper(properties);
        var registrations = propertyMapper.asClientRegistrations();
        log.info("Client registrations: {}", registrations.keySet());
        return new InMemoryClientRegistrationRepository(registrations);
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(
            ClientRegistrationRepository clientRegistrationRepository) {
        log.info("Creating InMemoryOAuth2AuthorizedClientService");
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public OAuth2AuthorizedClientRepository authenticatedPrincipalOAuth2AuthorizedClientRepository(
            OAuth2AuthorizedClientService authorizedClientService) {
        log.info("Creating AuthenticatedPrincipalOAuth2AuthorizedClientRepository");
        return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService);
    }
}
