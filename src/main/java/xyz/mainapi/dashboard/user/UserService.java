package xyz.mainapi.dashboard.user;

import java.io.InputStream;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface UserService {

    String PROFILE_PICTURE_BUCKET_NAME = "profile-picture";

    Optional<UserData> getCurrentUser(Authentication authentication);

    Page<UserLogEventData> getUserLogEvents(Authentication authentication, Pageable pageable);

    UserData updateCurrentUserPicture(Authentication authentication, InputStream picture, String contentType, long size) throws Exception;

}
