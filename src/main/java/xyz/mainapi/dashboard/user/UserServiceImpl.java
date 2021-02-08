package xyz.mainapi.dashboard.user;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;

import xyz.mainapi.dashboard.core.security.User;

@Service
class UserServiceImpl implements UserService {
    private static final UserMapper USER_MAPPER = UserMapper.INSTANCE;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ManagementAPI managementAPI;

    public UserServiceImpl(ManagementAPI managementAPI) {
        this.managementAPI = managementAPI;
    }

    @Override
    @Cacheable(cacheNames = "user", key = "#authentication.name")
    public Optional<User> getCurrentUser(Authentication authentication) {
        try {
            return Optional.of(managementAPI.users().get(authentication.getName(), null).execute())
                    .map(USER_MAPPER::toUser);
        } catch (Auth0Exception e) {
            logger.warn(e.getMessage(), e);
            return Optional.empty();
        }
    }
}
