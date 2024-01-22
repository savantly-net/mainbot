package net.savantly.mainbot.dom.chatmessage;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChatMessageContext {
    private ChatMessagePair messagePair;
}
