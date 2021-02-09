package xyz.mainapi.dashboard.user;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface UserService {
    Optional<UserData> getCurrentUser(Authentication authentication);

    Page<UserLogEventData> getUserLogEvents(Authentication authentication, Pageable pageable);
}