package xyz.mainapi.dashboard.app;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
interface AppRepository extends PagingAndSortingRepository<AppEntity, Long> {
    Page<AppEntity> findByCreatedBy(String createdBy, Pageable pageable);
}
