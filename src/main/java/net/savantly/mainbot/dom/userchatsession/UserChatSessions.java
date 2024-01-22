package net.savantly.mainbot.dom.userchatsession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.savantly.mainbot.dom.appuser.AppUsers;
import net.savantly.mainbot.dom.userchatsessionmemory.UserChatSessionMemory;
import net.savantly.mainbot.identity.UserDto;

@Service
@RequiredArgsConstructor
public class UserChatSessions {

    final private AppUsers users;
    final private UserChatSessionRepository repository;

    public UserChatSession save(UserChatSession userSession) {
        return repository.save(userSession);
    }

    public UserChatSession create(UserDto userDto) {
        var user = users.getById(userDto.getUid())
                .orElseGet(() -> users.create(userDto.getUid(), userDto.getName()));

        var emptyMemories = new ArrayList<UserChatSessionMemory>();

        UserChatSession userSession = new UserChatSession(
                generateSessionId(),
                user,
                emptyMemories);
        return save(userSession);
    }

    public Optional<UserChatSession> getById(String id) {
        return repository.findById(id);
    }

    private String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    public List<UserChatSession> getByUserId(String userId) {
        return repository.findByUserId(userId);
    }
}
