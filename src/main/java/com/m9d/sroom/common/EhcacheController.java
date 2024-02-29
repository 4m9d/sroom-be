package com.m9d.sroom.common;

import com.m9d.sroom.playlist.PlaylistService;
import com.m9d.sroom.video.VideoService;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class EhcacheController {

    private final CacheManager cacheManager;
    private final VideoService videoService;
    private final PlaylistService playlistService;
    public EhcacheController(CacheManager cacheManager, VideoService videoService, PlaylistService playlistService) {
        this.cacheManager = cacheManager;
        this.videoService = videoService;
        this.playlistService = playlistService;
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

//    @GetMapping("/ehcache/test/video/{videoCode}")
//    public Object getVideoInfo(@PathVariable String videoCode){
//        return videoService.getRecentVideo(videoCode);
//    }
//
//    @GetMapping("/ehcache/test/playlist/{playlistCode}")
//    public Object getPlaylistInfo(@PathVariable String playlistCode){
//        return playlistService.getRecentPlaylist(playlistCode);
//    }
}
