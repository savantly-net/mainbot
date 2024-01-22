package net.savantly.mainbot.dom.userchatsessionmemory;

import java.time.ZonedDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.experimental.Accessors;
import net.savantly.mainbot.dom.chatmessage.AiResponseMessage;
import net.savantly.mainbot.dom.userchatsession.UserChatSession;

@Data
@Accessors(chain = true)
@Entity
public class UserChatSessionMemory {
    
    @Id
    private String id;
    private ZonedDateTime created = ZonedDateTime.now();
    private String userSessionId;
    private String userId;


    @Enumerated(EnumType.STRING)
    private UserChatSessionMemoryState state = UserChatSessionMemoryState.IN_PROGRESS;

    @Column(length = 1024)
    private String userInput;

    @Column(length = 4096)
    private String generatedPrompt;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private AiResponseMessage systemResponse;

    @Column(length = 4096)
    private String systemResponseSummary;


    private UserChatSessionMemory() {}

    public static UserChatSessionMemory create(
        String requestId, 
        UserChatSession userSession, String userInput, AiResponseMessage systemResponse) {
        return new UserChatSessionMemory()
            .setId(requestId)
            .setUserInput(userInput)
            .setSystemResponse(systemResponse)
            .setUserId(userSession.getUser().getId())
            .setUserSessionId(userSession.getId());
    }
}
