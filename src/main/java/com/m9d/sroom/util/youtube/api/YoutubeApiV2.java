package com.m9d.sroom.util.youtube.api;

import com.m9d.sroom.util.youtube.resource.YoutubeReq;
import com.m9d.sroom.util.youtube.vo.playlist.PlaylistVo;
import com.m9d.sroom.util.youtube.vo.playlistitem.PlaylistVideoVo;
import com.m9d.sroom.util.youtube.vo.search.SearchVo;
import com.m9d.sroom.util.youtube.vo.video.VideoVo;

public interface YoutubeApiV2 {

    SearchVo getSearchVo(YoutubeReq resource);
    VideoVo getVideoVo(YoutubeReq resource);
    PlaylistVo getPlaylistVo(YoutubeReq resource);
    PlaylistVideoVo getPlaylistVideoVo(YoutubeReq resource);

    <T> T getYoutubeVo(YoutubeReq resource, Class<T> resultClass);
}
