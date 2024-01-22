package net.savantly.mainbot.dom.userchatsessionmemory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.savantly.mainbot.service.languagetools.LanguageTools;

@Configuration
public class UserChatSessionMemoryConfig {
    
    @Bean
    public UserChatSessionMemories userSessionMemories(UserChatSessionMemoryRepository repository, LanguageTools languageTools) {
        return new UserChatSessionMemoriesImpl(repository, languageTools);
    }
}
