package com.m9d.sroom.util.youtube;

import com.m9d.sroom.util.youtube.resource.*;
import com.m9d.sroom.util.youtube.vo.playlist.PlaylistVo;
import com.m9d.sroom.util.youtube.vo.playlistitem.PlaylistVideoVo;
import com.m9d.sroom.util.youtube.vo.search.SearchVo;
import com.m9d.sroom.util.youtube.vo.video.VideoVo;
import reactor.core.publisher.Mono;

public interface YoutubeApi {

    Mono<SearchVo> getSearchVo(YoutubeReq resource);
    Mono<VideoVo> getVideoVo(YoutubeReq resource);
    Mono<PlaylistVo> getPlaylistVo(YoutubeReq resource);
    Mono<PlaylistVideoVo> getPlaylistVideoVo(YoutubeReq resource);

    <T> Mono<T> getYoutubeVo(YoutubeReq resource, Class<T> resultClass);
}
