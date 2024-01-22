package net.savantly.mainbot.service.replicate;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReplicateClient {

    private static final String authHeader = "Authorization";

    private final RestTemplate restTemplate;
    private final String apiVersion;

    private String urlFormat = "/%s/%s";

    private Duration readTimeout = Duration.ofSeconds(120);

    private String formatUrl(String url) {
        return String.format(urlFormat, apiVersion, url);
    }

    public ReplicateClient(ReplicateConfig config, RestTemplateBuilder restTemplateBuilder) {
        log.info("creating ReplicateClient with config: {}", config);
        this.apiVersion = config.getVersion();
        this.restTemplate = restTemplateBuilder
            .rootUri(config.getUrl())
            .defaultHeader(authHeader, String.format("Token %s", config.getApiToken()))
            .setReadTimeout(readTimeout)
            .build();
    }

    /**
     * Uses a default backoffMillis of 1500 and retries of 10
     * @param <I>
     * @param <O>
     * @param request
     * @return
     */
    public <I extends ReplicateVersionHolder, O> ReplicateResponse<I,O> predictions(ReplicateRequest<I, O> request) {
        return predictions(request, Optional.empty(), 1500, 10);
    }

    private <I extends ReplicateVersionHolder, O> ReplicateResponse<I,O> predictions(ReplicateRequest<I, O> request, Optional<String> id) {
        var clazz = new ReplicateResponse<I,O>().getClass();
        var url = formatUrl("predictions");
        if (id.isPresent()) {
            url = formatUrl("predictions/" + id.get());
            log.debug("url: {}", url);
        }
        return restTemplate.postForObject(url, request, clazz);
    }

    public <I extends ReplicateVersionHolder, O> ReplicateResponse<I,O> predictions(ReplicateRequest<I, O> request, Optional<String> id, int backoffMillis, int retries) {

        var response = predictions(request, id);

        if (Objects.nonNull(response.getStatus()) && response.getStatus().toUpperCase().trim().contains("SUCCEEDED")) {
            return response;
        }
        log.debug("response: {}", response);
        log.info("retrying prediction request. retries left: {}", retries);

        if (retries > 0 && Objects.isNull(response.getError())) {
            try {
                Thread.sleep(backoffMillis);
            } catch (InterruptedException e) {
                log.error("interrupted", e);
            }
            return predictions(request, Optional.of(response.getId()), backoffMillis, retries - 1);
        }
        return response;
    }
}
