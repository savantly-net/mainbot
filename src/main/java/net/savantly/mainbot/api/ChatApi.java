package net.savantly.mainbot.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.savantly.mainbot.dom.chatmessage.ChatRequestDto;
import net.savantly.mainbot.dom.chatmessage.ChatResponseDto;
import net.savantly.mainbot.dom.chatsession.ChatSession;
import net.savantly.mainbot.dom.userchatsession.UserChatSessionDto;
import net.savantly.mainbot.dom.userchatsession.UserChatSessions;
import net.savantly.mainbot.dom.userchatsessionmemory.UserChatSessionMemories;
import net.savantly.mainbot.dom.userchatsessionmemory.UserChatSessionMemoryProjection;
import net.savantly.mainbot.identity.UserContext;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chat")
public class ChatApi {

    final ChatSession chatSession;
    final UserChatSessions userChatSessions;
    final UserContext userContext;
    final UserChatSessionMemories userChatSessionMemories;

    @PostMapping("/start")
    public UserChatSessionDto start() {
        var currentUser = userContext.getCurrentUser().orElseThrow();
        var chatSession = userChatSessions.create(currentUser);
        return chatSession.toDto();
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

    @GetMapping("/history")
    public ResponseEntity<List<UserChatSessionMemoryProjection>> history() {
        var currentUser = userContext.getCurrentUser().orElseThrow();
        var sessions = userChatSessionMemories.getLatestMemoryForEachSessionByUserId(currentUser.getUid());
        return ResponseEntity.ok(sessions);
    }

}
