package net.savantly.mainbot.http;

import java.io.IOException;
import java.util.List;

import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ForwardHeadersInterceptor implements HttpRequestInterceptor {

    final private List<String> forwardedHeaders;

    @Override
    public void process(HttpRequest request, EntityDetails entity, HttpContext context)
            throws HttpException, IOException {
        log.debug("forwarding headers: {} in context: {}", forwardedHeaders, context);
        for (var header : forwardedHeaders) {
            var value = context.getAttribute(header);
            if (value != null) {
                log.debug("found {} header in HttpContext and adding to HttpRequest", header);
                request.addHeader(header, value.toString());
            }
        }
    }

}
