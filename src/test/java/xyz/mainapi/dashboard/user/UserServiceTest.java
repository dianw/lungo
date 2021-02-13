package xyz.mainapi.dashboard.user;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import com.auth0.client.mgmt.ManagementAPI;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import xyz.mainapi.dashboard.core.data.MinioConfigProperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private Authentication authentication;
    @Mock
	private MinioClient minioClient;
    @Mock
    private MinioConfigProperties minioConfigProperties;

    @Spy
    private final ManagementAPI managementAPI = new ManagementAPI("mainapi.xyz", "");

    private UserService userService;

    @BeforeEach
    void beforeEach() {
        userService = new UserServiceImpl(managementAPI, minioClient, minioConfigProperties);
    }

    @Test
    void getCurrentUser() throws IOException, JSONException {
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse().setBody("{\"user_id\":\"123\"}"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));
        mockWebServer.start();

        ReflectionTestUtils.setField(managementAPI, "baseUrl", mockWebServer.url(""));

        when(authentication.getName()).thenReturn("test");
        Optional<UserData> user = userService.getCurrentUser(authentication);
        assertThat(user).isPresent();
        assertThat(user.get().getId()).isEqualTo("123");
        verify(authentication).getName();

        assertThat(userService.getCurrentUser(authentication)).isNotPresent();
    }

    @Test
    void getUserLogEvents() throws IOException {
        ObjectNode object = objectMapper.createObjectNode();
        object.put("_id", "test")
                .put("type", "s")
                .put("ip", "0.0.0.0")
                .put("date", Instant.now().toString())
                .put("user_id", "123")
                .putObject("location_info")
                .put("country_name", "Indonesia");
        ArrayNode array = objectMapper.createArrayNode().add(object);

        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse().setBody(array.toString()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));
        mockWebServer.start();

        ReflectionTestUtils.setField(managementAPI, "baseUrl", mockWebServer.url(""));

        when(authentication.getName()).thenReturn("test");
        Page<UserLogEventData> logs = userService.getUserLogEvents(authentication, PageRequest.of(0, 5));
        assertThat(logs).isNotNull()
            .isNotEmpty()
            .hasSize(1)
            .first().hasFieldOrPropertyWithValue("id", "test");
        assertThat(logs.iterator().next().getLocationInfo()).hasFieldOrPropertyWithValue("countryName", "Indonesia");
        verify(authentication).getName();

        assertThat(userService.getUserLogEvents(authentication, PageRequest.of(0, 5))).isEmpty();
    }

    @Test
    void testUpdateCurrentUserPicture() throws Exception {
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse().setBody("{\"user_id\":\"123\"}"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));
        mockWebServer.start();

        ReflectionTestUtils.setField(managementAPI, "baseUrl", mockWebServer.url(""));

        when(minioConfigProperties.getEndpoint()).thenReturn("https://example.com");
        when(authentication.getName()).thenReturn("test");
        when(minioClient.putObject(any()))
            .thenReturn(new ObjectWriteResponse(null, "bucket", "region", "object", "etag", "version"));
        UserData user = userService.updateCurrentUserPicture(authentication, new ByteArrayInputStream(new byte[0]), "image/png", 0);
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo("123");
        verify(minioConfigProperties).getEndpoint();
        verify(authentication).getName();
        verify(minioClient).putObject(any());
    }
}
