package net.savantly.mainbot.dom.userchatsession;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class UserChatSessionEntity {
    
    @Id
    private String id;
    private String userId;

}
