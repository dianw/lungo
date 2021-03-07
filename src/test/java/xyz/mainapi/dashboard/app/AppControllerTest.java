package xyz.mainapi.dashboard.app;

import java.util.Collections;
import java.util.Optional;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppController.class)
class AppControllerTest {
    private static final Jwt JWT = Jwt.withTokenValue("token")
        .header("alg", "none")
        .claim("sub", "user123")
        .build();

    @MockBean
    private AppService appService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        when(jwtDecoder.decode(anyString())).thenReturn(JWT);
    }

    @Test
    void findAppByCurrentUser() throws Exception {
        AppData response = ImmutableAppData.builder()
            .id(12345L)
            .appName("testing12345")
            .country("testing12345")
            .companyName("testing12345")
            .businessAddress("")
            .representativeName("")
            .build();
        when(appService.findAppByCurrentUser(any(), any())).then(invocation -> new PageImpl<>(Collections.singletonList(response)));

        mockMvc.perform(
            get("/api/apps")
                .header("Authorization", "Bearer " + JWT.getTokenValue()))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(new PageImpl<>(Collections.singletonList(response))), false));

        verify(jwtDecoder).decode(anyString());
        verify(appService).findAppByCurrentUser(any(), any());
    }

    @Test
    void findAppById() throws Exception {
        long id = 12345L;
        AppData response = ImmutableAppData.builder()
            .id(id)
            .appName("testing12345")
            .country("testing12345")
            .companyName("testing12345")
            .businessAddress("")
            .representativeName("")
            .build();

        when(appService.findAppById(id)).thenReturn(Optional.of(response));
        when(appService.findAppById(123L)).thenReturn(Optional.empty());

        mockMvc.perform(
            get("/api/apps/123")
                .header("Authorization", "Bearer " + JWT.getTokenValue()))
            .andExpect(status().isNotFound());

        mockMvc.perform(
            get("/api/apps/" + id)
                .header("Authorization", "Bearer " + JWT.getTokenValue()))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(response), false));

        verify(jwtDecoder, times(2)).decode(anyString());
        verify(appService, times(2)).findAppById(anyLong());
    }

    @Test
    void saveApp() throws Exception {
        long id = new Random().nextLong();

        when(appService.saveApp(any())).then(invocation -> {
            AppData data = invocation.getArgument(0);

            return ImmutableAppData.copyOf(data).withId(id);
        });

        AppData request = ImmutableAppData.builder()
            .appName("testing12345")
            .country("testing12345")
            .companyName("testing12345")
            .businessAddress("")
            .representativeName("")
            .build();

        mockMvc.perform(
            post("/api/apps")
                .header("Authorization", "Bearer " + JWT.getTokenValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(ImmutableAppData.builder().from(request).id(id).build()), false));

        verify(jwtDecoder).decode(anyString());
        verify(appService).saveApp(request);
    }

    @Test
    void updateApp() throws Exception {
        when(appService.updateApp(eq(123L), any(), any())).thenReturn(Optional.empty());
        when(appService.updateApp(eq(12345L), any(), any())).then(invocation -> {
            Long id = invocation.getArgument(0);
            AppData data = invocation.getArgument(2);

            return Optional.of(ImmutableAppData.copyOf(data).withId(id));
        });

        AppData request = ImmutableAppData.builder()
            .appName("testing12345")
            .country("testing12345")
            .companyName("testing12345")
            .businessAddress("")
            .representativeName("")
            .build();

        mockMvc.perform(
            put("/api/apps/123")
                .header("Authorization", "Bearer " + JWT.getTokenValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());

        mockMvc.perform(
            put("/api/apps/12345")
                .header("Authorization", "Bearer " + JWT.getTokenValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(ImmutableAppData.builder().from(request).id(12345L).build()), false));

        verify(jwtDecoder, times(2)).decode(anyString());
        verify(appService, times(2)).updateApp(anyLong(), any(), eq(request));
    }
}
