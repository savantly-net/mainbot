package net.savantly.mainbot.identity;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

/**
 * This class represents an event that is published when a user is authorized.
 * It contains the user DTO of the authorized user.
 */
@Getter
public class MeAuthorizedEvent extends ApplicationEvent {

    private final UserDto userDto;
    
    MeAuthorizedEvent(Object source, UserDto userDto) {
        super(source);
        this.userDto = userDto;
    }
}
