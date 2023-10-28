package com.m9d.sroom.youtube.api;

import com.m9d.sroom.youtube.resource.YoutubeReq;
import com.m9d.sroom.youtube.dto.playlist.PlaylistDto;
import com.m9d.sroom.youtube.dto.playlistitem.PlaylistVideoDto;
import com.m9d.sroom.youtube.dto.search.SearchDto;
import com.m9d.sroom.youtube.dto.video.VideoDto;

public interface YoutubeApi {

    SearchDto getSearchDto(YoutubeReq resource);
    VideoDto getVideoDto(YoutubeReq resource);
    PlaylistDto getPlaylistDto(YoutubeReq resource);
    PlaylistVideoDto getPlaylistVideoDto(YoutubeReq resource);

    <T> T getYoutubeDto(YoutubeReq resource, Class<T> resultClass);
}
