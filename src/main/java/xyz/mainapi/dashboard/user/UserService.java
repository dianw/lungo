package xyz.mainapi.dashboard.user;

import java.util.Optional;

import org.springframework.security.core.Authentication;

import xyz.mainapi.dashboard.core.security.User;

public interface UserService {
    Optional<User> getCurrentUser(Authentication authentication);
}