package net.savantly.mainbot.dom.promptcontributor;

import java.util.List;
import java.util.Optional;

import net.savantly.mainbot.dom.userchatsession.UserChatSession;
import net.savantly.mainbot.dom.userchatsessionmemory.UserChatSessionMemory;

public interface PromptContributor {
    
    Optional<String> process(UserChatSession playerSession, List<UserChatSessionMemory> memories, String userInput);
}
