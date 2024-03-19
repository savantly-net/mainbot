package net.savantly.mainbot.dom.chatmessage;

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
}
