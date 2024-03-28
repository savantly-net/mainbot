package net.savantly.mainbot.dom.documents;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DocumentAddRequest {
    
    private String id;
    private String namespace;
    private String uri;
    private String text;
    private boolean chunk;
    private Map<String, String> metadata = new HashMap<>();
    
}
