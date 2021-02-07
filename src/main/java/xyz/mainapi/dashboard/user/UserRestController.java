package xyz.mainapi.dashboard.user;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import xyz.mainapi.dashboard.core.security.User;

@RestController
@RequestMapping("/api/users/me")
public class UserRestController {
    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Mono<User> currentUser(Authentication authentication) {
        return userService.getCurrentUser(authentication);
    }
}
