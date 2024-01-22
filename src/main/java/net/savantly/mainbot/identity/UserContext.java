package net.savantly.mainbot.identity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserContext {

  final private UserDtoConverter converter;
  final private AnonymousUser anonymousUser;

  public Optional<UserDto> getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return Optional.of(anonymousUser.toDto());
    }

    var principal = authentication.getPrincipal();
    if (principal instanceof String) {
      return Optional.of(anonymousUser.toDto());
    }
    final Jwt jwt = (Jwt) (principal);
    return Optional.of(converter.fromJwt(jwt));
  }

  public Optional<String> getCurrentUserUID() {
    var optUser = getCurrentUser();
    if (optUser.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(optUser.get().getUid());
  }

  public List<String> getCurrentUserAuthorities() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return Collections.emptyList();
    }
    final List<String> authorities = new ArrayList<>();
    authorities.addAll(authentication.getAuthorities().stream().map(g -> g.getAuthority()).collect(Collectors.toList()));
    return authorities;
  }

  public boolean currentUserIsInGroup(String groupName) {
    var optUser = getCurrentUser();
    if (optUser.isEmpty()) {
      return false;
    }
    return optUser.get().getGroups().stream().anyMatch(s -> groupName.contentEquals(s));
  }

  public boolean hasAuthority(String authorityName) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }
    return authentication.getAuthorities().stream().anyMatch(a -> authorityName.contentEquals(a.getAuthority()));
  }

  public boolean hasAnyAuthority(String... authorityNames) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }
    return authentication.getAuthorities().stream().anyMatch(a -> {
      for (String authorityName : authorityNames) {
        if (authorityName.contentEquals(a.getAuthority())) {
          return true;
        }
      }
      return false;
    });
  }
}
