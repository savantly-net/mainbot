package net.savantly.mainbot.dom.chatmessage;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Entity
@Accessors(chain = true)
public class ResponseMessageDocRef {
    
    @Id
    private String id = java.util.UUID.randomUUID().toString();
    private String responseId;
    private String docId;
    private String type;
    private String namespace;
    private String url;
    private double score;
}
