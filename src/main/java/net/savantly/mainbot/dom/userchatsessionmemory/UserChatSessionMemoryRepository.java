package net.savantly.mainbot.dom.userchatsessionmemory;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface UserChatSessionMemoryRepository extends CrudRepository<UserChatSessionMemory, String> {

    List<UserChatSessionMemory> findByUserSessionId(String userSessionId);
    
}
