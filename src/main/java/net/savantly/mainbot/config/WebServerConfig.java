package net.savantly.mainbot.config;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class WebServerConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addConnectorCustomizers(connector -> {
            AbstractHttp11Protocol protocol = (AbstractHttp11Protocol) connector.getProtocolHandler();

            //protocol.setMaxKeepAliveRequests(100);
            protocol.setConnectionTimeout(secondsToMillis(120));


            log.info("####################################################################################");
            log.info("#");
            log.info("# TomcatCustomizer");
            log.info("#");
            log.info("# custom maxKeepAliveRequests {}", protocol.getMaxKeepAliveRequests());
            log.info("# origin keepalive timeout: {} ms", protocol.getKeepAliveTimeout());
            log.info("# keepalive timeout: {} ms", protocol.getKeepAliveTimeout());
            log.info("# connection timeout: {} ms", protocol.getConnectionTimeout());
            log.info("# max connections: {}", protocol.getMaxConnections());
            log.info("#");
            log.info(
                "####################################################################################");

        });
    }

    private int secondsToMillis(int seconds) {
        return seconds * 1000;
    }
}