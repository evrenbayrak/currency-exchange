package org.project.currencyexchange.controller;

import lombok.RequiredArgsConstructor;
import org.project.currencyexchange.service.CacheService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CacheController {

    private final CacheService cacheService;

    @GetMapping("/cache/evict")
    public void clearAllCaches(){
        cacheService.evictAllCache();
    }

}
