package net.savantly.mainbot.dom.userchatsessionmemory;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserChatSessionMemoryRepository extends CrudRepository<UserChatSessionMemory, String> {

    List<UserChatSessionMemory> findByUserSessionId(String userSessionId);

    @Query("""
        SELECT UserChatSessionMemoryProjection(m.id, m.created, m.userSessionId, m.userId, m.userInput)
        FROM UserChatSessionMemory m 
        JOIN (
            SELECT MAX(m2.created) as created, m2.userSessionId as userSessionId
            FROM UserChatSessionMemory m2
            WHERE m2.userId = ?1
            GROUP BY m2.userSessionId
            ) as latest
        WHERE m.userSessionId = latest.userSessionId AND m.created = latest.created
        """)
    List<UserChatSessionMemoryProjection> findLatestByUserIdGroupBySession(String userId);
    
}
