package net.savantly.mainbot.dom.userchatsession;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class UserChatSessionRepositoryInMemory implements UserChatSessionRepository {

    private final HashMap<String, UserChatSession> sessions = new HashMap<>();

    @Override
    public UserChatSession save(UserChatSession userSession) {
        if (sessions.containsKey(userSession.getId())) {
            sessions.replace(userSession.getId(), userSession);
        } else {
            sessions.put(userSession.getId(), userSession);
        }
        return userSession;
    }

    @Override
    public Optional<UserChatSession> findById(String id) {
        return Optional.ofNullable(sessions.get(id));
    }

    @Override
    public List<UserChatSession> findByUserId(String userId) {
        return sessions.values().stream().filter(s -> s.getUserId().equals(userId)).toList();
    }

}
