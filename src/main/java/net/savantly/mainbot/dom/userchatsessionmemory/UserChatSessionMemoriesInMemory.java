package net.savantly.mainbot.dom.userchatsessionmemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserChatSessionMemoriesInMemory implements UserChatSessionMemories {

    private final HashMap<String, List<UserChatSessionMemory>> memories = new HashMap<>();
    

    @Override
    public List<UserChatSessionMemory> getByUserSessionId(String userSessionId) {
        var mem = memories.get(userSessionId);
        if (mem == null) {
            return new ArrayList<UserChatSessionMemory>();
        }
        return mem;
    }

    @Override
    public void store(UserChatSessionMemory memory) {
        if (memories.containsKey(memory.getUserSessionId())) {
            memories.get(memory.getUserSessionId()).add(memory);
        } else {
            var mem = new ArrayList<UserChatSessionMemory>();
            mem.add(memory);
            memories.put(memory.getUserSessionId(), mem);
        }
    }

    @Override
    public String getSummarizedMemories(String userSessionId) {
        var sb = new StringBuilder();
        var memories = getByUserSessionId(userSessionId);
        for (UserChatSessionMemory memory : memories) {
            sb.append(memory.getSystemResponse());
        }
        return sb.toString();
    }

    @Override
    public UserChatSessionMemory getById(String id) {
        return memories.values().stream().flatMap(List::stream).filter(m -> m.getId().equals(id)).findFirst().orElseThrow();
    }

}
