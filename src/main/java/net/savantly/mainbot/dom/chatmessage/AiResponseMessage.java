package net.savantly.mainbot.dom.chatmessage;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Entity
@Accessors(chain = true)
public class AiResponseMessage {

    public AiResponseMessage() {
    }

    @Id
    private String id = java.util.UUID.randomUUID().toString();

    @Column(length = 4096)
    @JsonAlias("ai_message")
    private String aiMessage;

    @Transient
    private List<ResponseMessageDocRef> docRefs = new ArrayList<>();

    @Transient
    public AiResponseMessageDto toDto() {
        AiResponseMessageDto dto = new AiResponseMessageDto()
            .setId(this.getId())
            .setAiMessage(this.getAiMessage());
        this.getDocRefs().forEach(docRef -> dto.addDocRef(docRef.toDto()));
        return dto;
    }
}
