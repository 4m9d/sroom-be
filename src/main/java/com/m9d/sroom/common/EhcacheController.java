package com.m9d.sroom.common;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class EhcacheController {

    private final CacheManager cacheManager;

    public EhcacheController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @GetMapping("/ehcache")
    public Object findAll(){
        return cacheManager.getCacheNames().stream()
                .map(cacheName -> {
                    EhCacheCache cache = (EhCacheCache) cacheManager.getCache(cacheName);
                    Ehcache ehcache = cache.getNativeCache();
                    Map<String, List<String>> entry = new HashMap<>();

                    ehcache.getKeys().forEach(key -> {
                        Element element = ehcache.get(key);
                        if (element != null) {
                            entry.computeIfAbsent(cacheName, k -> new ArrayList<>()).add(element.toString());
                        }
                    });

                    return entry;
                })
                .collect(Collectors.toList());
    }
}
