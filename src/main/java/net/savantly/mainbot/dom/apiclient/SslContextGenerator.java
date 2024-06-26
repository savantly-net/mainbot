package net.savantly.mainbot.dom.apiclient;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Objects;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;

import net.savantly.mainbot.x509.KeystoreProvider;

public class SslContextGenerator {

    public static class Builder {

        private boolean insecureSsl;

        private boolean useTrustStore = false;
        private String trustStorePath;
        private String trustStorePassword;
        private String trustStoreType;

        private boolean useClientCert = false;
        private String keyStorePath;
        private String keyStorePassword;
        private String keyStoreType;
        private KeyStore keyStore;

        public Builder withTrustStore(String path, String password, String type) {
            this.useTrustStore = true;
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

        public Builder withInsecureSSL() {
            this.insecureSsl = true;
            this.trustStorePath = null;
            this.trustStorePassword = null;
            return this;
        }

        private TrustStrategy getTrustStrategy() {
            if (insecureSsl) {
                return new TrustAllStrategy();
            }
            return new TrustSelfSignedStrategy();
        }

        public SSLContext build() throws Exception {

            if (Objects.isNull(this.keyStore) && keyStorePath != null) {
                keyStore = KeyStore.getInstance(keyStoreType);
                try (FileInputStream keyStoreInputStream = new FileInputStream(keyStorePath)) {
                    keyStore.load(keyStoreInputStream, keyStorePassword.toCharArray());
                }
            }

            var sslContextBuilder = SSLContextBuilder.create();

            if (this.useTrustStore) {
                KeyStore trustStore = KeyStore.getInstance(trustStoreType);
                try (FileInputStream trustStoreInputStream = new FileInputStream(trustStorePath)) {
                    trustStore.load(trustStoreInputStream, trustStorePassword.toCharArray());
                }
                sslContextBuilder.loadTrustMaterial(trustStore, getTrustStrategy());
            } else {
                sslContextBuilder.loadTrustMaterial(getTrustStrategy());
            }

            if (this.useClientCert) {
                sslContextBuilder.loadKeyMaterial(keyStore, keyStorePassword.toCharArray());
            }

            var sslContext = sslContextBuilder.build();
            return sslContext;
        }
    }
}
