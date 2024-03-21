package net.savantly.mainbot.dom.chatsession;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import net.savantly.mainbot.dom.documents.DocumentService;
import net.savantly.mainbot.dom.userchatsessionmemory.UserChatSessionMemories;
import net.savantly.mainbot.service.languagetools.LanguageTools;

@Configuration
@Data
@ConfigurationProperties(prefix = "chat.session")
public class ChatSessionConfiguration {

    private ChatSessionConfigurationProperties config = new ChatSessionConfigurationProperties();

    @Bean
    public ChatMessageSender chatMessageSender(LanguageTools chat, UserChatSessionMemories playerSessionMemories,
            DocumentService documentService) {
        return new ChatMessageSender(config, chat, playerSessionMemories, documentService);
    }
}
