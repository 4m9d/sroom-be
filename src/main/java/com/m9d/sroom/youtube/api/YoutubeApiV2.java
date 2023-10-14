package com.m9d.sroom.youtube.api;

import com.m9d.sroom.youtube.resource.YoutubeReq;
import com.m9d.sroom.youtube.vo.playlist.PlaylistVo;
import com.m9d.sroom.youtube.vo.playlistitem.PlaylistVideoVo;
import com.m9d.sroom.youtube.vo.search.SearchVo;
import com.m9d.sroom.youtube.vo.video.VideoVo;

public interface YoutubeApiV2 {

    SearchVo getSearchVo(YoutubeReq resource);
    VideoVo getVideoVo(YoutubeReq resource);
    PlaylistVo getPlaylistVo(YoutubeReq resource);
    PlaylistVideoVo getPlaylistVideoVo(YoutubeReq resource);

    <T> T getYoutubeVo(YoutubeReq resource, Class<T> resultClass);
}
