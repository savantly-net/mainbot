package net.savantly.mainbot.http;

import java.io.IOException;

import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class OAuth2Interceptor implements HttpRequestInterceptor {

    final OAuth2AuthorizedClientManager clientManager;
    final String oauth2ClientRegistrationId;

    @Override
    public void process(HttpRequest request, EntityDetails entity, HttpContext context)
            throws HttpException, IOException {
        log.debug("Checking for OAuth2 authorization header");
        if (request.containsHeader("Authorization")) {
            log.debug("Request already contains Authorization header");
            return;
        }
        request.addHeader("Authorization", createAuthorizationHeaderValue());
        log.debug("Request headers: {}", request.getHeaders().toString());
    }

    private String createAuthorizationHeaderValue() {
        log.debug(
                "Creating Authorization header value from OAuth2AuthorizedClientManager with client registration id: {}",
                oauth2ClientRegistrationId);
        var principal = "mainbot";
        var authorizedClient = this.clientManager
                .authorize(OAuth2AuthorizeRequest.withClientRegistrationId(oauth2ClientRegistrationId)
                        .principal(principal)
                        .build());
        log.debug("Authorized client: {}", authorizedClient.getClientRegistration().getRegistrationId());
        return "Bearer " + authorizedClient.getAccessToken().getTokenValue();
    }

}
