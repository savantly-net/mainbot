package net.savantly.mainbot.dom.userchatsessionmemory;

import java.util.List;

public interface UserChatSessionMemories {

    List<UserChatSessionMemory> getByUserSessionId(String userSessionId);

    void store(UserChatSessionMemory memory);

    String getSummarizedMemories(String userSessionId);

    UserChatSessionMemory getById(String id);

    List<UserChatSessionMemoryProjection> getLatestMemoryForEachSessionByUserId(String userId);
}
