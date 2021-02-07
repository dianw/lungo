package xyz.mainapi.dashboard.core.data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(createConcurrentMapCaches("user"));
        return cacheManager;
    }

    private List<Cache> createConcurrentMapCaches(String... cacheNames) {
        return Stream.of(cacheNames)
                .map(ConcurrentMapCache::new)
                .collect(Collectors.toList());
    }
}
