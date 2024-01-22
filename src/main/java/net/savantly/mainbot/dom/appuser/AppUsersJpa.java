package net.savantly.mainbot.dom.appuser;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AppUsersJpa implements AppUsers {

    private final AppUserRepository repository;

    @Override
    public Optional<AppUser> getById(String id) {
        return repository.findById(id);
    }

    @Override
    public AppUser create(String id, String name) {
        return repository.save(new AppUser().setId(id).setName(name));
    }

    
    
}
