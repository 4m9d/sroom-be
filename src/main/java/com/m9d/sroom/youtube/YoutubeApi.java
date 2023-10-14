package com.m9d.sroom.youtube;

import com.m9d.sroom.util.youtube.resource.*;
import com.m9d.sroom.youtube.vo.playlist.PlaylistVo;
import com.m9d.sroom.youtube.vo.playlistitem.PlaylistVideoVo;
import com.m9d.sroom.youtube.vo.search.SearchVo;
import com.m9d.sroom.youtube.vo.video.VideoVo;
import com.m9d.sroom.youtube.resource.YoutubeReq;
import reactor.core.publisher.Mono;

public interface YoutubeApi {

    Mono<SearchVo> getSearchVo(YoutubeReq resource);
    Mono<VideoVo> getVideoVo(YoutubeReq resource);
    Mono<PlaylistVo> getPlaylistVo(YoutubeReq resource);
    Mono<PlaylistVideoVo> getPlaylistVideoVo(YoutubeReq resource);

    <T> Mono<T> getYoutubeVo(YoutubeReq resource, Class<T> resultClass);
}
