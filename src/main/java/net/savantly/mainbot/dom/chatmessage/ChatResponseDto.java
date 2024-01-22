package net.savantly.mainbot.dom.chatmessage;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChatResponseDto {
    
    private String systemMessage;
    private String userMessage;
    private String aiMessage;
}
