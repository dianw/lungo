package xyz.mainapi.dashboard.user;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.UserFilter;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import xyz.mainapi.dashboard.core.security.User;

@Service
class UserServiceImpl implements UserService {
    private static final UserMapper USER_MAPPER = UserMapper.INSTANCE;
    private final ManagementAPI managementAPI;

    public UserServiceImpl(ManagementAPI managementAPI) {
        this.managementAPI = managementAPI;
    }

    @Override
    public Mono<User> getCurrentUser(Authentication authentication) {
        return Mono
                .fromCallable(() -> managementAPI.users().get(authentication.getName(), new UserFilter()).execute())
                .map(USER_MAPPER::toUser)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
