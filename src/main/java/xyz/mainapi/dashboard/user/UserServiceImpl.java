package xyz.mainapi.dashboard.user;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
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
import com.auth0.json.mgmt.users.User;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import xyz.mainapi.dashboard.core.data.MinioConfigProperties;

@Service
class UserServiceImpl implements UserService {
    private static final UserMapper USER_MAPPER = UserMapper.INSTANCE;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final TimeBasedGenerator timeBasedGenerator = Generators.timeBasedGenerator();
    private final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    private final ManagementAPI managementAPI;
    private final MinioClient minioClient;
    private final MinioConfigProperties minioConfigProperties;

    public UserServiceImpl(ManagementAPI managementAPI, MinioClient minioClient, MinioConfigProperties minioConfigProperties) {
        this.managementAPI = managementAPI;
        this.minioClient = minioClient;
        this.minioConfigProperties = minioConfigProperties;
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

    @Override
    @CacheEvict(cacheNames = "user", key = "#authentication.name")
    public UserData updateCurrentUserPicture(Authentication authentication, InputStream picture, String contentType, long size) throws Exception {
        String username = encoder.encodeToString(authentication.getName().getBytes(StandardCharsets.UTF_8));
        String imageObjectName = username + "/" + timeBasedGenerator.generate().toString();

        // upload picture to S3
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
            .bucket(PROFILE_PICTURE_BUCKET_NAME)
            .object(imageObjectName)
            .contentType(contentType)
            .stream(picture, size, -1)
            .build();
        ObjectWriteResponse resp = minioClient.putObject(putObjectArgs);

        // generate picture URL
        String pictureUrl = minioConfigProperties.getEndpoint() + "/" + resp.bucket() + "/" + resp.object();

        // update user, put the picture url in userMetadata
        User user = new User();
        user.setUserMetadata(Collections.singletonMap(UserMapper.PROPS_USER_METADATA_PICTURE_URL, pictureUrl));
        User updatedUser = managementAPI.users().update(username, user).execute();

        return USER_MAPPER.toUserData(updatedUser);
    }

}
