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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UserContext {

  final private UserDtoConverter converter;
  final private AnonymousUser anonymousUser;

  public Optional<UserDto> getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      log.debug("no authentication found");
      return Optional.of(anonymousUser.toDto());
    }

    var principal = authentication.getPrincipal();

    if (principal == null) {
      log.debug("no principal found");
      return Optional.of(anonymousUser.toDto());
    }

    if (principal instanceof UserDto) {
      log.debug("principal is UserDto: {}", principal);
      return Optional.of((UserDto) principal);
    }

    if (principal instanceof String) {
      log.debug("principal is String: {}", principal);
      return Optional.of(anonymousUser.toDto());
    }
    final Jwt jwt = (Jwt) (principal);
    var dto = converter.fromJwt(jwt);
    log.debug("principal is Jwt: {}", dto);
    return Optional.of(dto);
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
      log.debug("no authentication found");
      return Collections.emptyList();
    }
    final List<String> authorities = new ArrayList<>();
    authorities
        .addAll(authentication.getAuthorities().stream().map(g -> g.getAuthority()).collect(Collectors.toList()));
    log.debug("authorities: {}", authorities);
    return authorities;
  }

  public boolean currentUserIsInGroup(String groupName) {
    var optUser = getCurrentUser();
    if (optUser.isEmpty()) {
      log.debug("no user found");
      return false;
    }
    var isInGroup = optUser.get().getGroups().stream().anyMatch(s -> groupName.contentEquals(s));
    log.debug("user is in group ({}): {}", groupName, isInGroup);
    return isInGroup;
  }

  public boolean hasAuthority(String authorityName) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      log.debug("no authentication found");
      return false;
    }
    var hasAuthorityResult = authentication.getAuthorities().stream()
        .anyMatch(a -> authorityName.contentEquals(a.getAuthority()));
    log.debug("user has authority ({}): {}", authorityName, hasAuthorityResult);
    return hasAuthorityResult;
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
