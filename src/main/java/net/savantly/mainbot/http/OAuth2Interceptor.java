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
        log.debug("Adding OAuth2 Authorization header");
        request.addHeader("Authorization", createAuthorizationHeaderValue());
        log.debug("Request headers: {}", request.getHeaders().toString());
    }

    private String createAuthorizationHeaderValue() {
        var principal = "mainbot";
        var authorizedClient = this.clientManager
                .authorize(OAuth2AuthorizeRequest.withClientRegistrationId(oauth2ClientRegistrationId)
                        .principal(principal)
                        .build());
        log.debug("Authorized client: {}", authorizedClient.getClientRegistration().getRegistrationId());
        return "Bearer " + authorizedClient.getAccessToken().getTokenValue();
    }

}
