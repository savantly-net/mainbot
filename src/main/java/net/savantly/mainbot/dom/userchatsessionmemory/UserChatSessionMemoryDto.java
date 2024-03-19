package net.savantly.mainbot.dom.userchatsessionmemory;

import java.time.ZonedDateTime;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.experimental.Accessors;
import net.savantly.mainbot.dom.chatmessage.AiResponseMessageDto;

@Data
@Accessors(chain = true)
public class UserChatSessionMemoryDto {

    private String id;
    private ZonedDateTime created = ZonedDateTime.now();
    private String userSessionId;
    private String userId;

    @Enumerated(EnumType.STRING)
    private UserChatSessionMemoryState state = UserChatSessionMemoryState.IN_PROGRESS;
    private String userInput;

    private AiResponseMessageDto systemResponse;
    private String systemResponseSummary;
}
