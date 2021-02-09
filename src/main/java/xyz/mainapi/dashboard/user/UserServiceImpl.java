package xyz.mainapi.dashboard.user;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.LogEventFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.logevents.LogEventsPage;

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
    public Optional<UserData> getCurrentUser(Authentication authentication) {
        try {
            return Optional.of(managementAPI.users().get(authentication.getName(), null).execute())
                    .map(USER_MAPPER::toUserData);
        } catch (Auth0Exception e) {
            logger.warn(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Page<UserLogEventData> getUserLogEvents(Authentication authentication, Pageable pageable) {
        try {
            LogEventsPage logEventsPage = managementAPI.users()
                    .getLogEvents(authentication.getName(), new LogEventFilter()
                            .withPage(pageable.getPageNumber(), pageable.getPageSize()))
                    .execute();
            return new PageImpl<>(USER_MAPPER.toUserLogEventDataList(logEventsPage.getItems()));
        } catch (Auth0Exception e) {
            logger.warn(e.getMessage(), e);
            return Page.empty();
        }
    }
}
