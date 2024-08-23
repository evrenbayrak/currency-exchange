package org.project.currencyexchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class CacheService {

    private final CacheManager cacheManager;

    public void evictAllCache() {
        cacheManager.getCacheNames()
                .parallelStream()
                .forEach( cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
        log.info("All caches are evicted.");
    }

}
