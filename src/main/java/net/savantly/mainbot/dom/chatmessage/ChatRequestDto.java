package net.savantly.mainbot.dom.chatmessage;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChatRequestDto {
    
    @NotBlank
    private String sessionId;

    @NotBlank
    private String message;
}
