package net.savantly.mainbot.dom.userchatsession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import net.savantly.mainbot.dom.appuser.AppUser;
import net.savantly.mainbot.dom.chatmessage.AiResponseMessage;
import net.savantly.mainbot.dom.userchatsessionmemory.UserChatSessionMemory;

public class UserChatSession {

    @Builder
    public UserChatSession(String id, AppUser user, Collection<UserChatSessionMemory> memories) {
        this.id = id;
        this.user = user;
        this.memories = memories;
    }

    @Getter
    @NotEmpty
    private final String id;

    @Getter
    @NotNull
    private final AppUser user;

    // TODO: make this configurable
    @Getter
    private final String namespace = "/admin";

    private final Collection<UserChatSessionMemory> memories;

    public UserChatSession addMemory(UserChatSessionMemory memory) {
        memories.add(memory);
        return this;
    }

    // read only
    public Collection<UserChatSessionMemory> getMemories() {
        var collection = new ArrayList<UserChatSessionMemory>(memories);
        // sort
        collection.sort(Comparator.comparing(UserChatSessionMemory::getCreated));
        return collection;
    }

    public Optional<UserChatSessionMemory> getlatestMemory() {
        return memories.stream().max(Comparator.comparing(UserChatSessionMemory::getCreated));
    }

    public String getLastPrompt() {
        return getlatestMemory().map(m -> m.getGeneratedPrompt()).orElse(null);
    }

    public Optional<AiResponseMessage> getLastResponse() {
        return getlatestMemory().map(m -> m.getSystemResponse());
    }

    public String getLastUserInput() {
        return getlatestMemory().map(m -> m.getUserInput()).orElse(null);
    }

    public String getUserId() {
        return user.getId();
    }

    public UserChatSessionDto toDto() {
        return new UserChatSessionDto()
            .setId(this.getId())
            .setUser(this.getUser().toDto())
            .setNamespace(this.getNamespace())
            .setMemories(this.getMemories().stream().map(UserChatSessionMemory::toDto).toList());
    }
}
