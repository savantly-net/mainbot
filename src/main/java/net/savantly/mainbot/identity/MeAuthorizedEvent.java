package net.savantly.mainbot.identity;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class MeAuthorizedEvent extends ApplicationEvent {

    private final UserDto userDto;
    
    MeAuthorizedEvent(Object source, UserDto userDto) {
        super(source);
        this.userDto = userDto;
    }
}
