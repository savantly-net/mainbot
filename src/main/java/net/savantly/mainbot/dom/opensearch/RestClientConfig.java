package net.savantly.mainbot.dom.opensearch;

import java.util.Objects;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.core5.function.Factory;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.reactor.ssl.TlsDetails;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

import lombok.RequiredArgsConstructor;
import net.savantly.mainbot.dom.apiclient.SslContextGenerator;
import net.savantly.mainbot.dom.opensearch.OpenSearchConfiguration.AuthenticationConfig;
import net.savantly.mainbot.http.ForwardHeadersInterceptor;
import net.savantly.mainbot.http.OAuth2Interceptor;

@RequiredArgsConstructor
public class RestClientConfig {

    final OpenSearchConfiguration config;
    final AuthenticationConfig authenticationConfig;
    final OAuth2AuthorizedClientManager clientManager;

    public OpenSearchClient opensearchClient() {

        var sslContextBuilder = new SslContextGenerator.Builder();

        var trustStorePath = config.getTrustStorePath();
        var trustStorePassword = config.getTrustStorePassword();
        if (Objects.nonNull(trustStorePath)
                && trustStorePath.length() > 0
                && Objects.nonNull(trustStorePassword)
                && trustStorePassword.length() > 0) {
            sslContextBuilder.withTrustStore(trustStorePath, trustStorePassword, "jks");
        }
        var clientCertPath = config.getClientCertPath();
        var clientKeyPath = config.getClientKeyPath();
        if (Objects.nonNull(clientCertPath)
                && clientCertPath.length() > 0
                && Objects.nonNull(clientKeyPath)
                && clientKeyPath.length() > 0) {
            sslContextBuilder.withPEMKeys(clientCertPath, clientKeyPath,
                    config.getTrustStorePassword());
        }

        if (config.isUseInsecureSSL()) {
            sslContextBuilder.withInsecureSSL();
        }
        try {
            final SSLContext sslContext = sslContextBuilder.build();

            var host = HttpHost.create(config.getUrl());
            HostnameVerifier hostnameVerifier = (config.isSkipHostnameVerification()) ? (hostName, sslSession) -> true
                    : null;

            final ApacheHttpClient5TransportBuilder builder = ApacheHttpClient5TransportBuilder.builder(host);
            builder.setHttpClientConfigCallback(httpClientBuilder -> {
                var tlsStrategyBuilder = ClientTlsStrategyBuilder.create()
                        .setSslContext(sslContext)
                        .setHostnameVerifier(hostnameVerifier)
                        // See https://issues.apache.org/jira/browse/HTTPCLIENT-2219
                        .setTlsDetailsFactory(new Factory<SSLEngine, TlsDetails>() {
                            @Override
                            public TlsDetails create(final SSLEngine sslEngine) {
                                return new TlsDetails(sslEngine.getSession(), sslEngine.getApplicationProtocol());
                            }
                        });

                final TlsStrategy tlsStrategy = tlsStrategyBuilder.build();

                if (authenticationConfig.getMethod() == OpenSearchConfiguration.AuthenticationMethod.OAUTH2) {
                    httpClientBuilder.addRequestInterceptorFirst(new OAuth2Interceptor(clientManager,
                    authenticationConfig.getOauth2().getClientRegistrationId()));
                }

                if (authenticationConfig.getMethod() == OpenSearchConfiguration.AuthenticationMethod.BASIC) {
                    httpClientBuilder.setDefaultCredentialsProvider((request, context) -> {
                        return new UsernamePasswordCredentials(authenticationConfig.getBasic().getUsername(),
                        authenticationConfig.getBasic().getPassword().toCharArray());
                    });
                }

                if (config.getForwardedHeaders() != null && !config.getForwardedHeaders().isEmpty()) {
                    httpClientBuilder
                            .addRequestInterceptorFirst(new ForwardHeadersInterceptor(config.getForwardedHeaders()));
                }

                final PoolingAsyncClientConnectionManager connectionManager = PoolingAsyncClientConnectionManagerBuilder
                        .create()
                        .setTlsStrategy(tlsStrategy)
                        .build();
                

                return httpClientBuilder
                        .setConnectionManager(connectionManager);
            });

            builder.setPathPrefix(config.getPathPrefix());

            final OpenSearchTransport transport = builder.build();
            OpenSearchClient client = new OpenSearchClient(transport);
            return client;
        } catch (Exception e) {
            throw new RuntimeException("Problem configuring the SSL context", e);
        }
    }
}