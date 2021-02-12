package xyz.mainapi.dashboard.user;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetCurrentUser() throws Exception {
        mockMvc.perform(get("/api/users/me")).andExpect(status().isUnauthorized());

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "user123")
                .build();

        when(jwtDecoder.decode(anyString())).thenReturn(jwt);
        when(userService.getCurrentUser(any())).then(invocation -> {
            Authentication auth = invocation.getArgument(0, Authentication.class);
            return Optional.of(ImmutableUserData.builder().id(auth.getName()).build());
        });

        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + jwt.getTokenValue()))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":\"user123\"}", false));

        verify(jwtDecoder).decode(eq(jwt.getTokenValue()));
        verify(userService).getCurrentUser(any());
    }

    @Test
    void testGetCurrentUserLogEvent() throws Exception {
        mockMvc.perform(get("/api/users/me/logs")).andExpect(status().isUnauthorized());

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "user123")
                .build();

        PageImpl<UserLogEventData> events = new PageImpl<>(Collections.emptyList());

        when(jwtDecoder.decode(anyString())).thenReturn(jwt);
        when(userService.getUserLogEvents(any(), any())).thenReturn(events);

        mockMvc.perform(get("/api/users/me/logs")
                .header("Authorization", "Bearer " + jwt.getTokenValue()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(events), false));

        verify(jwtDecoder).decode(jwt.getTokenValue());
        verify(userService).getUserLogEvents(any(), any());
    }

    @Test
    void testUpdateUserPicture() throws Exception {
        mockMvc.perform(put("/api/users/me/picture")
            .contentType("image/jpg")
            .content(new byte[5]))
            .andExpect(status().isUnauthorized());

        Jwt jwt = Jwt.withTokenValue("token")
            .header("alg", "none")
            .claim("sub", "user123")
            .build();

        UserData userData = ImmutableUserData.builder().id("123").build();

        when(jwtDecoder.decode(anyString())).thenReturn(jwt);
        when(userService.updateCurrentUserPicture(any(), any(), anyString(), anyLong()))
            .thenReturn(userData);

        mockMvc.perform(put("/api/users/me/picture")
            .header("Authorization", "Bearer " + jwt.getTokenValue())
            .contentType("text/plain"))
            .andExpect(status().isUnsupportedMediaType());

        mockMvc.perform(put("/api/users/me/picture")
            .header("Authorization", "Bearer " + jwt.getTokenValue())
            .contentType("image/jpg")
            .content(new byte[0]))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(userData), false));

        verify(jwtDecoder, atLeastOnce()).decode(jwt.getTokenValue());
        verify(userService).updateCurrentUserPicture(any(), any(), anyString(), anyLong());
    }
}
