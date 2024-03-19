package net.savantly.mainbot.dom.chatmessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AiResponseMessageDto {

    private String id;
    private String aiMessage;
    private List<ResponseMessageDocRefDto> docRefs = new ArrayList<>();

    public AiResponseMessageDto addDocRef(ResponseMessageDocRefDto docRef) {
        this.docRefs.add(docRef);
        return this;
    }

    public AiResponseMessageDto addDocRef(Collection<ResponseMessageDocRefDto> docRefs) {
        this.docRefs.addAll(docRefs);
        return this;
    }
}
