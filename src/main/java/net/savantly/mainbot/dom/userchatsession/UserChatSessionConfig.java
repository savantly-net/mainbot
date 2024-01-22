package net.savantly.mainbot.dom.userchatsession;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.savantly.mainbot.dom.appuser.AppUsers;
import net.savantly.mainbot.dom.userchatsessionmemory.UserChatSessionMemories;

@Configuration
public class UserChatSessionConfig {
    
    @Bean
    public UserChatSessionRepository userSessionRepository(UserChatSessionEntityRepository repository, AppUsers users, UserChatSessionMemories memories) {
        //return new PlayerSessionRepositoryInMemory();
        return new UserChatSessionRepositoryJpa(repository, users, memories);
    }
}
