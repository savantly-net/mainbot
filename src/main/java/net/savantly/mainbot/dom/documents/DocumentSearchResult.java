package net.savantly.mainbot.dom.documents;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DocumentSearchResult {

    private String embeddingId;
    private String text;
    private double score;
    private String uri;

    private Map<String, String> metadata = new HashMap<>();

}
