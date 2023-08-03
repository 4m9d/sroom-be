package com.m9d.sroom.util.youtube;

import com.m9d.sroom.util.youtube.resource.*;
import com.m9d.sroom.util.youtube.vo.playlist.PlaylistVo;
import com.m9d.sroom.util.youtube.vo.playlistitem.PlaylistVideoVo;
import com.m9d.sroom.util.youtube.vo.search.SearchVo;
import com.m9d.sroom.util.youtube.vo.video.VideoVo;
import reactor.core.publisher.Mono;

public interface YoutubeApi {

    Mono<SearchVo> getSearchVo(YoutubeResource resource);
    Mono<VideoVo> getVideoVo(YoutubeResource resource);
    Mono<PlaylistVo> getPlaylistVo(YoutubeResource resource);
    Mono<PlaylistVideoVo> getPlaylistVideoVo(YoutubeResource resource);

    <T> Mono<T> getYoutubeVo(YoutubeResource resource, Class<T> resultClass);
}
