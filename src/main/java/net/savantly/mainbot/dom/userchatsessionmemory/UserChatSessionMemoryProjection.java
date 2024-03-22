package net.savantly.mainbot.dom.userchatsessionmemory;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class UserChatSessionMemoryProjection {

    public UserChatSessionMemoryProjection(UserChatSessionMemory memory) {
        this.id = memory.getId();
        this.created = memory.getCreated();
        this.userSessionId = memory.getUserSessionId();
        this.userId = memory.getUserId();
        this.userInput = memory.getUserInput();
    }
    
    private String id;
    private ZonedDateTime created;
    private String userSessionId;
    private String userId;
    private String userInput;
}
