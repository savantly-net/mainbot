package net.savantly.mainbot.dom.userchatsession;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import net.savantly.mainbot.dom.appuser.AppUsers;
import net.savantly.mainbot.dom.userchatsessionmemory.UserChatSessionMemories;

@RequiredArgsConstructor
public class UserChatSessionRepositoryJpa implements UserChatSessionRepository {

    final private UserChatSessionEntityRepository repository;
    final private AppUsers users;
    final private UserChatSessionMemories memories;

    @Override
    public UserChatSession save(UserChatSession userSession) {
        final UserChatSessionEntity saved = repository.save(toEntity(userSession));
        return toDomain(saved);
    }

    @Override
    public Optional<UserChatSession> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<UserChatSession> findByUserId(String userId) {
        return repository.findByUserId(userId).stream().map(mapper -> toDomain(mapper)).toList();
    }

    private UserChatSessionEntity toEntity(UserChatSession userSession) {
        final UserChatSessionEntity entity = repository.findById(userSession.getId())
                .orElseGet(() -> new UserChatSessionEntity());
        entity.setId(userSession.getId())
                .setUserId(userSession.getUser().getId());
        return entity;
    }

    private UserChatSession toDomain(UserChatSessionEntity saved) {
        final String id = saved.getId();
        final String userId = saved.getUserId();
        final var user = users.getById(userId).orElseThrow();
        final var sessionMemories = memories.getByUserSessionId(id);

        var dto = new UserChatSession.UserChatSessionBuilder()
                .id(id)
                .user(user)
                .memories(sessionMemories)
                .build();

        return dto;
    }

}
