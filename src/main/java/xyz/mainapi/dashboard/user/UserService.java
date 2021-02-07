package xyz.mainapi.dashboard.user;

import org.springframework.security.core.Authentication;

import reactor.core.publisher.Mono;
import xyz.mainapi.dashboard.core.security.User;

public interface UserService {
    Mono<User> getCurrentUser(Authentication authentication);
}