package net.savantly.mainbot.service.replicate;

import java.time.ZonedDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReplicateResponse<I,O> {
    
    private String id;
    private String version;
    private I input;
    private O output;
    private ZonedDateTime completedAt;
    private ZonedDateTime createdAt;
    private ZonedDateTime startedAt;
    private String error;
    private String status;
    private Map<String, Object> metrics;

}
