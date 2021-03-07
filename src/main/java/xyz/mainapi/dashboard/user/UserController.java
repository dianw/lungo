package xyz.mainapi.dashboard.user;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/users/me")
@ApiResponse(responseCode = "401", description = "Unauthorized")
@ApiResponse(
    responseCode = "200",
    description = "Success",
    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
)
public class UserController {
    public static final String DOCS_TAG = "Users";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(tags = DOCS_TAG, summary = "Get current user data")
    @GetMapping
    public ResponseEntity<UserData> getCurrentUser(Authentication authentication) {
        return userService.getCurrentUser(authentication)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(tags = DOCS_TAG, summary = "Get current user operation logs")
    @GetMapping("/logs")
    public Page<UserLogEventData> getCurrentUserLogEvent(Authentication authentication, @PageableDefault Pageable pageable) {
        return userService.getUserLogEvents(authentication, pageable);
    }

    @Operation(tags = DOCS_TAG, summary = "Update user profile picture")
    @PutMapping(path = "/picture", consumes = "image/*")
    public UserData updateUserPicture(Authentication authentication, HttpServletRequest request) throws Exception {
        try (InputStream picture = request.getInputStream()) {
            return userService.updateCurrentUserPicture(authentication, picture, request.getContentType(), request.getContentLengthLong());
        }
    }
}
