package net.savantly.mainbot.dom.chatsession;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.savantly.mainbot.dom.documents.DocumentService;
import net.savantly.mainbot.dom.userchatsessionmemory.UserChatSessionMemories;
import net.savantly.mainbot.service.languagetools.LanguageTools;

@Configuration
public class ChatSessionConfiguration {

    @Bean
    public ChatMessageSender chatMessageSender(LanguageTools chat, UserChatSessionMemories playerSessionMemories,
            DocumentService documentService) {
        return new ChatMessageSender(chat, playerSessionMemories, documentService);
    }
}
