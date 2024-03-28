package net.savantly.mainbot.dom.apiclient;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Objects;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.BasicHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import net.savantly.mainbot.x509.KeystoreProvider;

public class ApiClient {

    public static class Builder {

        private boolean useSSL = false;
        private String trustStorePath;
        private String trustStorePassword;
        private String trustStoreType;

        private boolean useClientCert = false;
        private String keyStorePath;
        private String keyStorePassword;
        private String keyStoreType;
        private KeyStore keyStore;

        public Builder withTrustStore(String path, String password, String type) {
            this.useSSL = true;
            this.trustStorePath = path;
            this.trustStorePassword = password;
            this.trustStoreType = type;
            return this;
        }

        public Builder withKeyStore(String path, String password, String type) {
            this.useClientCert = true;
            this.keyStorePath = path;
            this.keyStorePassword = password;
            this.keyStoreType = type;
            return this;
        }

        public Builder withPEMKeys(String certPath, String keyPath, String password) {
            this.useClientCert = true;
            this.keyStore = new KeystoreProvider().loadPEMKeyStore(certPath, keyPath, password);
            this.keyStorePassword = password;
            return this;
        }

        public RestTemplate build() throws Exception {
            KeyStore trustStore = KeyStore.getInstance(trustStoreType);
            try (FileInputStream trustStoreInputStream = new FileInputStream(trustStorePath)) {
                trustStore.load(trustStoreInputStream, trustStorePassword.toCharArray());
            }

            if (Objects.isNull(this.keyStore) && keyStorePath != null) {
                keyStore = KeyStore.getInstance(keyStoreType);
                try (FileInputStream keyStoreInputStream = new FileInputStream(keyStorePath)) {
                    keyStore.load(keyStoreInputStream, keyStorePassword.toCharArray());
                }
            }

            var sslContextBuilder = SSLContextBuilder.create();

            if (this.useSSL) {
                sslContextBuilder.loadTrustMaterial(trustStore, null);
            }

            if (this.useClientCert) {
                sslContextBuilder.loadKeyMaterial(keyStore, keyStorePassword.toCharArray());
            }

            var sslContext = sslContextBuilder.build();

            final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                    NoopHostnameVerifier.INSTANCE);
            final Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory>create()
                    .register("https", sslsf)
                    .register("http", new PlainConnectionSocketFactory())
                    .build();

            final BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager(
                    socketFactoryRegistry);

            var httpClient = HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .build();

            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient((HttpClient) httpClient);

            return new RestTemplate(requestFactory);
        }
    }
}
