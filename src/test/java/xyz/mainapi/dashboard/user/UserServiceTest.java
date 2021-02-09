package xyz.mainapi.dashboard.user;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import com.auth0.client.mgmt.ManagementAPI;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private Authentication authentication;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ManagementAPI managementAPI = new ManagementAPI("mainapi.xyz", "");
    private final UserService userService = new UserServiceImpl(managementAPI);

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
        assertThat(logs).isNotNull();
        assertThat(logs).isNotEmpty();
        assertThat(logs).hasSize(1);
        assertThat(logs).first().hasFieldOrPropertyWithValue("id", "test");
        assertThat(logs.iterator().next().getLocationInfo()).hasFieldOrPropertyWithValue("countryName", "Indonesia");
        verify(authentication).getName();

        assertThat(userService.getUserLogEvents(authentication, PageRequest.of(0, 5))).isEmpty();
    }
}