package com.m9d.sroom.youtube;

import com.m9d.sroom.youtube.api.YoutubeApiV2;
import com.m9d.sroom.util.youtube.dto.*;
import com.m9d.sroom.youtube.dto.*;
import com.m9d.sroom.youtube.resource.PlaylistItemReq;
import com.m9d.sroom.youtube.resource.PlaylistReq;
import com.m9d.sroom.youtube.resource.SearchReq;
import com.m9d.sroom.youtube.resource.VideoReq;
import com.m9d.sroom.youtube.vo.playlist.PlaylistVo;
import com.m9d.sroom.youtube.vo.playlistitem.PlaylistVideoVo;
import com.m9d.sroom.youtube.vo.search.SearchVo;
import com.m9d.sroom.youtube.vo.video.VideoVo;
import org.springframework.stereotype.Service;

import static com.m9d.sroom.lecture.constant.LectureConstant.*;

@Service
public class YoutubeService {

    private final YoutubeApiV2 youtubeApi;

    public YoutubeService(YoutubeApiV2 youtubeApi) {
        this.youtubeApi = youtubeApi;
    }

    public PlaylistInfo getPlaylistInfo(String code) {
        PlaylistVo playlistVo = youtubeApi.getPlaylistVo(PlaylistReq.builder()
                .playlistCode(code)
                .build());

        return new PlaylistInfo(playlistVo);
    }

    public VideoInfo getVideoInfo(String code) {
        VideoVo videoVo = youtubeApi.getVideoVo(VideoReq.builder()
                .videoCode(code)
                .build());

        return new VideoInfo(videoVo);
    }

    public PlaylistItemInfo getPlaylistItemInfo(String code, String nextPageToken, int limit) {
        PlaylistVideoVo playlistVideoVo = youtubeApi.getPlaylistVideoVo(PlaylistItemReq.builder()
                .playlistCode(code)
                .nextPageToken(nextPageToken)
                .limit(limit)
                .build());

        return new PlaylistItemInfo(playlistVideoVo);
    }

    public SearchInfo getSearchInfo(String keyword, String nextPageToken, int limit, String filter) {
        SearchVo searchVo = youtubeApi.getSearchVo(SearchReq.builder()
                .keyword(keyword)
                .filter(filter)
                .limit(limit)
                .pageToken(nextPageToken)
                .build());

        return new SearchInfo(searchVo);
    }

    public boolean checkIfPlaylist(String lectureCode) {
        String firstTwoCharacters = lectureCode.substring(LECTURE_CODE_START_INDEX, LECTURE_CODE_PLAYLIST_INDICATOR_LENGTH);
        return firstTwoCharacters.equals(PLAYLIST_CODE_INDICATOR);
    }

    public boolean isPrivacyStatusUnusable(PlaylistVideoInfo videoInfo) {
        String privacyStatus = videoInfo.getPrivacyStatus();
        return privacyStatus.equals(YoutubeConstant.JSONNODE_PRIVATE) || privacyStatus.equals(YoutubeConstant.JSONNODE_UNSPECIFIED);
    }
}
