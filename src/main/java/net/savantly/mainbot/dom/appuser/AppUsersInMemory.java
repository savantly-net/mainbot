package net.savantly.mainbot.dom.appuser;

import java.util.Optional;

public class AppUsersInMemory implements AppUsers {

    @Override
    public Optional<AppUser> getById(String id) {
        return Optional.of(new AppUser()
                .setId(id)
                .setName("test"));
    }

    @Override
    public AppUser create(String id, String name) {
        return new AppUser()
                .setId(id)
                .setName(name);
    }

}
