package com.m9d.sroom.youtube;

import com.m9d.sroom.playlist.vo.Playlist;
import com.m9d.sroom.video.vo.Video;
import com.m9d.sroom.youtube.api.YoutubeApi;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
@Slf4j
public class YoutubeMapper {

    private final YoutubeApi youtubeApi;

    public YoutubeMapper(YoutubeApi youtubeApi) {
        this.youtubeApi = youtubeApi;
    }

    public Playlist getPlaylist(String code, int reviewCount, int accumulatedRating) {
        PlaylistDto playlistVo = youtubeApi.getPlaylistDto(PlaylistReq.builder()
                .playlistCode(code)
                .build());

        return playlistVo.toPlaylist(reviewCount, accumulatedRating);
    }

    public Video getVideo(String code, int reviewCount, int accumulatedRating) {
        VideoDto videoVo = youtubeApi.getVideoDto(VideoReq.builder()
                .videoCode(code)
                .build());

        return videoVo.toVideo(reviewCount, accumulatedRating);
    }

    public PlaylistItemInfo getPlaylistItemInfo(String code, String nextPageToken, int limit) {
        PlaylistVideoDto playlistVideoVo = youtubeApi.getPlaylistVideoDto(PlaylistItemReq.builder()
                .playlistCode(code)
                .nextPageToken(nextPageToken)
                .limit(limit)
                .build());

        return playlistVideoVo.toPlaylistItemInfo();
    }

    public SearchInfo getSearchInfo(String keyword, String nextPageToken, int limit, String filter) {
        SearchDto searchVo = youtubeApi.getSearchDto(SearchReq.builder()
                .keyword(URLEncoder.encode(keyword, StandardCharsets.UTF_8))
                .filter(filter)
                .limit(limit)
                .pageToken(nextPageToken)
                .build());

        log.info("subject = youtubeSearch, keyword = {}, nextTokenUsed = {}, filter = {}",
                keyword, !Objects.equals(nextPageToken, ""), filter);
        return searchVo.toSearchInfo();
    }
}
