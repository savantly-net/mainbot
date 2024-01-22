package net.savantly.mainbot.dom.documents;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DocumentQuery {
    
    private String text;
    private int maxResults = 10;
    private double minScore = 0.5;
}
