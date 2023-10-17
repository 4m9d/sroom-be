package com.m9d.sroom.youtube.api;

import com.m9d.sroom.youtube.resource.YoutubeReq;
import com.m9d.sroom.youtube.dto.playlist.PlaylistDto;
import com.m9d.sroom.youtube.dto.playlistitem.PlaylistVideoDto;
import com.m9d.sroom.youtube.dto.search.SearchDto;
import com.m9d.sroom.youtube.dto.video.VideoDto;
import reactor.core.publisher.Mono;

public interface YoutubeApi {

    Mono<SearchDto> getSearchVo(YoutubeReq resource);
    Mono<VideoDto> getVideoVo(YoutubeReq resource);
    Mono<PlaylistDto> getPlaylistVo(YoutubeReq resource);
    Mono<PlaylistVideoDto> getPlaylistVideoVo(YoutubeReq resource);

    <T> Mono<T> getYoutubeVo(YoutubeReq resource, Class<T> resultClass);
}
