package net.savantly.mainbot.dom.chatsession;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.savantly.mainbot.dom.chatmessage.AiResponseMessage;
import net.savantly.mainbot.dom.chatmessage.ChatResponseDto;
import net.savantly.mainbot.dom.userchatsession.UserChatSession;
import net.savantly.mainbot.dom.userchatsessionmemory.UserChatSessionMemories;
import net.savantly.mainbot.dom.userchatsessionmemory.UserChatSessionMemory;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatSession {

    final private UserChatSessionMemories playerSessionMemories;
    final private ChatMessageSender chatMessageSender;

    final ObjectMapper mapper = new ObjectMapper();

    @Async
    public CompletableFuture<ChatResponseDto> takeTurnAsync(UserChatSession playerSession, String userInput) {
        return CompletableFuture.supplyAsync(() -> {
            return sendMessage(playerSession, userInput);
        });
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public ChatResponseDto sendMessage(UserChatSession playerSession, String userInput) {
        final String requestId = UUID.randomUUID().toString();

        final AiResponseMessage aiMessage = chatMessageSender.sendMessage(requestId, playerSession, userInput);

        var playerSessionMemory = UserChatSessionMemory.create(requestId,
                playerSession, userInput, aiMessage);
        playerSessionMemories.store(playerSessionMemory);

        playerSession.addMemory(playerSessionMemory);

        return new ChatResponseDto()
                .setUserMessage(userInput)
                .setAiMessage(aiMessage.getAiMessage());
    }

}
