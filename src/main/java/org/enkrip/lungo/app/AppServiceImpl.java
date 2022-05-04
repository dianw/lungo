package org.enkrip.lungo.app;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class AppServiceImpl implements AppService {
    private final AppMapper appMapper = AppMapper.INSTANCE;
    private final AppRepository appRepository;

    AppServiceImpl(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    @Override
    public Page<AppData> findAppByCurrentUser(Authentication auth, Pageable pageable) {
        return appRepository.findByCreatedBy(auth.getName(), pageable)
            .map(appMapper::toAppData);
    }

    @Override
    public Optional<AppData> findAppById(Long id) {
        return appRepository.findById(id)
            .map(appMapper::toAppData);
    }

    @Override
    @Transactional
    public AppData saveApp(AppData appData) {
        AppEntity entity = appMapper.toAppEntity(appData);
        return appMapper.toAppData(appRepository.save(entity));
    }

    @Override
    @Transactional
    public Optional<AppData> updateApp(@NotNull Long id, Authentication auth, AppData appData) {
        return appRepository.findById(id)
            // only app creator can perform update
            .filter(e -> e.getCreatedBy().filter(auth.getName()::equals).isPresent())
            // perform update
            .map(e -> appMapper.updateAppEntity(e, appData))
            // map entity back to dto
            .map(appMapper::toAppData);
    }
}
