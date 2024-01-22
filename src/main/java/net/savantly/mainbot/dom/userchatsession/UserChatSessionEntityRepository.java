package net.savantly.mainbot.dom.userchatsession;

import java.util.List;

import net.savantly.mainbot.repository.CustomRepository;

public interface UserChatSessionEntityRepository extends CustomRepository<UserChatSessionEntity, String> {

    List<UserChatSessionEntity> findByUserId(String userId);
    
}
