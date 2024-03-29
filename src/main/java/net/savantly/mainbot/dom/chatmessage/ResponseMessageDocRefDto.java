package net.savantly.mainbot.dom.chatmessage;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ResponseMessageDocRefDto {
    
    private String id;
    private String responseId;
    private String docId;
    private String type;
    private String namespace;
    private String url;
    private double score;
    public Map<String, String> metadata = new HashMap<>();
}
