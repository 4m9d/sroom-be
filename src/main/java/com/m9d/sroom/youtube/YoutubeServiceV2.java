package com.m9d.sroom.youtube;

import com.m9d.sroom.common.vo.Playlist;
import com.m9d.sroom.common.vo.Video;
import com.m9d.sroom.youtube.api.YoutubeApiV2;
import com.m9d.sroom.youtube.vo.PlaylistItemInfo;
import com.m9d.sroom.youtube.vo.SearchInfo;
import com.m9d.sroom.youtube.resource.PlaylistItemReq;
import com.m9d.sroom.youtube.resource.PlaylistReq;
import com.m9d.sroom.youtube.resource.SearchReq;
import com.m9d.sroom.youtube.resource.VideoReq;
import com.m9d.sroom.youtube.dto.playlist.PlaylistDto;
import com.m9d.sroom.youtube.dto.playlistitem.PlaylistVideoDto;
import com.m9d.sroom.youtube.dto.search.SearchDto;
import com.m9d.sroom.youtube.dto.video.VideoDto;
import org.springframework.stereotype.Service;

@Service
public class YoutubeServiceV2 {

    private final YoutubeApiV2 youtubeApi;

    public YoutubeServiceV2(YoutubeApiV2 youtubeApi) {
        this.youtubeApi = youtubeApi;
    }

    public Playlist getPlaylist(String code) {
        PlaylistDto playlistVo = youtubeApi.getPlaylistVo(PlaylistReq.builder()
                .playlistCode(code)
                .build());

        return playlistVo.toPlaylist();
    }

    public Video getVideo(String code) {
        VideoDto videoVo = youtubeApi.getVideoVo(VideoReq.builder()
                .videoCode(code)
                .build());

        return videoVo.toVideo();
    }

    public PlaylistItemInfo getPlaylistItemInfo(String code, String nextPageToken, int limit) {
        PlaylistVideoDto playlistVideoVo = youtubeApi.getPlaylistVideoVo(PlaylistItemReq.builder()
                .playlistCode(code)
                .nextPageToken(nextPageToken)
                .limit(limit)
                .build());

        return playlistVideoVo.toPlaylistItemInfo();
    }

    public SearchInfo getSearchInfo(String keyword, String nextPageToken, int limit, String filter) {
        SearchDto searchVo = youtubeApi.getSearchVo(SearchReq.builder()
                .keyword(keyword)
                .filter(filter)
                .limit(limit)
                .pageToken(nextPageToken)
                .build());

        return searchVo.toSearchInfo();
    }
}
