package net.savantly.mainbot.dom.appuser;

import java.util.Optional;

public interface AppUsers {

    Optional<AppUser> getById(String id);

    AppUser create(String id, String name);
}
