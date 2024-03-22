package net.savantly.mainbot.dom.chatmessage;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
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

    @Transient
    public Map<String, String> metadata = new HashMap<>();

    @Transient
    public ResponseMessageDocRefDto toDto() {
        return new ResponseMessageDocRefDto()
            .setId(this.getId())
            .setResponseId(this.getResponseId())
            .setDocId(this.getDocId())
            .setType(this.getType())
            .setNamespace(this.getNamespace())
            .setUrl(this.getUrl())
            .setScore(this.getScore())
            .setMetadata(this.getMetadata());
    }
}
