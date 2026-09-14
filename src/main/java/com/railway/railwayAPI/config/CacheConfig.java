package com.railway.railwayAPI.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

/**
 * Caffeine cache configuration.
 *
 * <p>Previously a single unbounded cache named {@code "cache"} was shared by
 * both the station autocomplete and the search-filter services, and the TTL
 * property was misspelled ({@code spring.cache.caffeine.spec.autocompleteCache})
 * so it was silently ignored — meaning no eviction and no size bound at all.
 *
 * <p>This replaces that with two purpose-built caches:
 * <ul>
 *   <li>{@link #AUTOCOMPLETE_CACHE} — station list is effectively static, so a
 *       long TTL and larger size are fine.</li>
 *   <li>{@link #SEARCH_FILTERS_CACHE} — derived from live availability, so it
 *       uses a short TTL to avoid serving stale filter option sets.</li>
 * </ul>
 * Names are constants so the {@code @Cacheable} annotations can reference them
 * without drifting apart from this config.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String AUTOCOMPLETE_CACHE = "autocomplete";
    public static final String SEARCH_FILTERS_CACHE = "searchFilters";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCache autocomplete = new CaffeineCache(AUTOCOMPLETE_CACHE,
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofDays(7))
                        .maximumSize(10_000)
                        .build());

        CaffeineCache searchFilters = new CaffeineCache(SEARCH_FILTERS_CACHE,
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(5))
                        .maximumSize(1_000)
                        .build());

        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(List.of(autocomplete, searchFilters));
        return manager;
    }
}
