package net.savantly.mainbot.security.oauth2;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This class represents a service that is used to get the access token for a
 * given client registration id.
 * <p>
 * It's assumed you have properties similar to the following in your
 * application.properties file (or equivalent yaml).
 * <p>
 * In the following example, the client registration id is "myclient".
 * 
 * <pre>
spring.security.oauth2.client.registration.myclient.client-id=client-id
spring.security.oauth2.client.registration.myclient.client-secret=client-secret
spring.security.oauth2.client.registration.myclient.authorization-grant-type=client_credentials
spring.security.oauth2.client.registration.myclient.scope=your-scope
spring.security.oauth2.client.provider.myclient.token-uri=https://example.com/oauth/token
 * </pre>
 * <p>
 */
@RequiredArgsConstructor
@Slf4j
public class OAuth2ClientService {

    final private OAuth2AuthorizedClientService authorizedClientService;

    public String getAccessToken(String clientRegistrationId) {
        log.info("Getting access token for client registration id: {}", clientRegistrationId);
        OAuth2AuthorizedClient client = this.authorizedClientService.loadAuthorizedClient(
                clientRegistrationId,
                clientRegistrationId);

        if (client == null) {
            throw new IllegalStateException("Client not configured properly");
        }

        return client.getAccessToken().getTokenValue();
    }
}
