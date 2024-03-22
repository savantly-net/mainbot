package net.savantly.mainbot.dom.userchatsessionmemory;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.savantly.mainbot.service.languagetools.LanguageTools;

@RequiredArgsConstructor
public class UserChatSessionMemoriesImpl implements UserChatSessionMemories {

    private final UserChatSessionMemoryRepository repository;
    private final LanguageTools languageTools;

    @Override
    public List<UserChatSessionMemory> getByUserSessionId(String userSessionId) {
        return repository.findByUserSessionId(userSessionId);
    }

    @Override
    @Transactional
    public void store(UserChatSessionMemory memory) {
        repository.save(memory);
    }

    @Override
    public String getSummarizedMemories(String userSessionId) {
        var sb = new StringBuilder();
        var memories = getByUserSessionId(userSessionId);

        for (UserChatSessionMemory memory : memories) {
            sb.append(memory.getSystemResponseSummary());
        }
        if (sb.length() == 0) {
            return "";
        }
        // only take the last 3000 characters
        if (sb.length() > 3000) {
            sb.delete(0, sb.length() - 3000);
        }
        return languageTools.summarizeEvents(sb.toString());
    }

    @Override
    public UserChatSessionMemory getById(String id) {
        return repository.findById(id).orElseThrow();
    }

    @Override
    public List<UserChatSessionMemoryProjection> getLatestMemoryForEachSessionByUserId(String userId) {
        return repository.findLatestByUserIdGroupBySession(userId);
    }

}
