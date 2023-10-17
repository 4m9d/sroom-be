package com.m9d.sroom.youtube.api;

import com.m9d.sroom.youtube.resource.YoutubeReq;
import com.m9d.sroom.youtube.dto.playlist.PlaylistDto;
import com.m9d.sroom.youtube.dto.playlistitem.PlaylistVideoDto;
import com.m9d.sroom.youtube.dto.search.SearchDto;
import com.m9d.sroom.youtube.dto.video.VideoDto;

public interface YoutubeApiV2 {

    SearchDto getSearchVo(YoutubeReq resource);
    VideoDto getVideoVo(YoutubeReq resource);
    PlaylistDto getPlaylistVo(YoutubeReq resource);
    PlaylistVideoDto getPlaylistVideoVo(YoutubeReq resource);

    <T> T getYoutubeVo(YoutubeReq resource, Class<T> resultClass);
}
