package net.savantly.mainbot.dom.appuser;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppUserConfig {
    
    @Bean
    public AppUsers appUsers(AppUserRepository repository) {
        //return new PlayersInMemory();
        return new AppUsersJpa(repository);
    }
}
