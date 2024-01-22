package net.savantly.mainbot.dom.userchatsession;

import java.util.List;
import java.util.Optional;

public interface UserChatSessionRepository {

    UserChatSession save(UserChatSession userSession);

    Optional<UserChatSession> findById(String id);

    List<UserChatSession> findByUserId(String userId);
    
}
