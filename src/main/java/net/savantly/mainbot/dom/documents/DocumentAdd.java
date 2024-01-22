package net.savantly.mainbot.dom.documents;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DocumentAdd {
    
    private String namespace;
    private String text;
    private Map<String, String> metadata = new HashMap<>();
    
}
