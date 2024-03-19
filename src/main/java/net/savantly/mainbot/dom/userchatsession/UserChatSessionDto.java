package net.savantly.mainbot.dom.userchatsession;

import java.util.Collection;

import lombok.Data;
import lombok.experimental.Accessors;
import net.savantly.mainbot.dom.userchatsessionmemory.UserChatSessionMemoryDto;
import net.savantly.mainbot.identity.UserDto;

@Data
@Accessors(chain = true)
public class UserChatSessionDto {

    private String id;
    private UserDto user;
    private String namespace;
    private Collection<UserChatSessionMemoryDto> memories;
}
