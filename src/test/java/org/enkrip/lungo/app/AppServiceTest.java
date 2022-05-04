package org.enkrip.lungo.app;

import java.util.Collections;
import java.util.Optional;
import java.util.Random;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppServiceTest {

    @Mock
    private Authentication authentication;

    @Mock
    private AppRepository appRepository;

    private AppService appService;

    @BeforeEach
    void beforeEach() {
        appService = new AppServiceImpl(appRepository);
    }

    @Test
    void findAppByCurrentUser() {
        when(appRepository.findByCreatedBy(eq("test123"), any())).then(invocation -> {
            AppEntity entity = new AppEntity();
            entity.setId(12345L);
            entity.setAppName("testing123");
            entity.setCountry("");
            entity.setCompanyName("");
            entity.setCompanyName("");
            entity.setBusinessAddress("");
            entity.setRepresentativeName("");

            return new PageImpl<>(Collections.singletonList(entity));
        });
        when(authentication.getName()).thenReturn("test123");

        Page<AppData> apps = appService.findAppByCurrentUser(authentication, Pageable.unpaged());
        Assertions.assertThat(apps).hasSize(1);
        Assertions.assertThat(apps).first()
            .hasFieldOrPropertyWithValue("id", Optional.of(12345L))
            .hasFieldOrPropertyWithValue("appName", "testing123");

        verify(appRepository).findByCreatedBy(eq("test123"), any());
        verify(authentication).getName();
    }

    @Test
    void findAppById() {
        when(appRepository.findById(123L)).thenReturn(Optional.empty());
        when(appRepository.findById(12345L)).then(invocation -> {
            AppEntity entity = new AppEntity();
            entity.setId(12345L);
            entity.setAppName("testing123");
            entity.setCountry("");
            entity.setCompanyName("");
            entity.setCompanyName("");
            entity.setBusinessAddress("");
            entity.setRepresentativeName("");

            return Optional.of(entity);
        });

        Assertions.assertThat(appService.findAppById(123L)).isNotPresent();
        Assertions.assertThat(appService.findAppById(12345L))
            .isPresent()
            .get()
            .hasFieldOrPropertyWithValue("id", Optional.of(12345L))
            .hasFieldOrPropertyWithValue("appName", "testing123");

        verify(appRepository).findById(123L);
        verify(appRepository).findById(12345L);
    }

    @Test
    void saveApp() {
        long id = new Random().nextLong();
        when(appRepository.save(any())).then(invocation -> {
            AppEntity entity = invocation.getArgument(0);
            entity.setId(id);
            return entity;
        });

        AppData app = appService.saveApp(ImmutableAppData.builder()
            .appName("testing123")
            .country("")
            .companyName("testing123")
            .businessAddress("")
            .representativeName("")
            .build()
        );
        assertThat(app.getId()).isPresent()
            .get()
            .isEqualTo(id);
        assertThat(app)
            .hasFieldOrPropertyWithValue("appName", "testing123")
            .hasFieldOrPropertyWithValue("companyName", "testing123");

        verify(appRepository).save(any());
    }

    @Test
    void updateApp() {
        when(authentication.getName()).thenReturn("test123");
        when(appRepository.findById(12345L)).then(invocation -> {
            AppEntity entity = new AppEntity();
            entity.setId(12345L);
            entity.setAppName("testing123");
            entity.setCountry("");
            entity.setCompanyName("");
            entity.setCompanyName("");
            entity.setBusinessAddress("");
            entity.setRepresentativeName("");
            entity.setCreatedBy("test123");

            return Optional.of(entity);
        });

        Optional<AppData> app = appService.updateApp(12345L, authentication, ImmutableAppData.builder()
            .appName("testing12345")
            .country("testing12345")
            .companyName("testing12345")
            .businessAddress("")
            .representativeName("")
            .build());

        Assertions.assertThat(app).isPresent()
            .get()
            .hasFieldOrPropertyWithValue("appName", "testing12345")
            .hasFieldOrPropertyWithValue("country", "testing12345")
            .hasFieldOrPropertyWithValue("companyName", "testing12345");
        assertThat(app.get().getId()).isPresent().get().isEqualTo(12345L);

        verify(authentication).getName();
        verify(appRepository).findById(12345L);
    }
}
