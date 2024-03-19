package net.savantly.mainbot.security;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

import lombok.RequiredArgsConstructor;
import net.savantly.mainbot.dom.appuser.AppUsers;

/**
 * This class represents an event listener that listens for successful authentication events.
 * It is responsible for creating a new user in the database if the user does not exist.
 */
@RequiredArgsConstructor
public class LoginSuccessListener implements ApplicationListener<AuthenticationSuccessEvent>{

    private final AppUsers users;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent evt) {

        var id = evt.getAuthentication().getName();
        var exists = users.getById(id).isPresent();

        if (!exists) {
            users.create(id, id);
        }

    } 
}