package org.enkrip.lungo.app;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface AppService {
    Page<AppData> findAppByCurrentUser(Authentication auth, Pageable pageable);

    Optional<AppData> findAppById(Long id);

    AppData saveApp(AppData appData);

    Optional<AppData> updateApp(Long id, Authentication auth, AppData appData);
}
