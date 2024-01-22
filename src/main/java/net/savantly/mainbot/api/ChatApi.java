package net.savantly.mainbot.api;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.savantly.mainbot.dom.chatmessage.ChatRequestDto;
import net.savantly.mainbot.dom.chatmessage.ChatResponseDto;
import net.savantly.mainbot.dom.chatsession.ChatSession;
import net.savantly.mainbot.dom.userchatsession.UserChatSession;
import net.savantly.mainbot.dom.userchatsession.UserChatSessions;
import net.savantly.mainbot.identity.UserContext;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chat")
public class ChatApi {

    final ChatSession chatSession;
    final UserChatSessions userChatSessions;
    final UserContext userContext;

    @PostMapping("/start")
    public UserChatSession start() {
        var currentUser = userContext.getCurrentUser().orElseThrow();
        return userChatSessions.create(currentUser);
    }

    @PostMapping("/session")
    public ChatResponseDto chat(@RequestBody @Validated ChatRequestDto request) {
        var currentUser = userContext.getCurrentUser().orElseThrow();
        var session = userChatSessions.getById(request.getSessionId()).orElseThrow();
        if (!session.getUserId().equals(currentUser.getUid())) {
            throw new RuntimeException("session does not belong to current user");
        }
        return chatSession.sendMessage(session, request.getMessage());
    }

}
