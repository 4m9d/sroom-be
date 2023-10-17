package com.m9d.sroom.lecture;

import com.m9d.sroom.lecture.dto.response.KeywordSearchResponse;
import com.m9d.sroom.lecture.dto.response.LectureResponse;
import com.m9d.sroom.playlist.PlaylistService;
import com.m9d.sroom.video.VideoService;
import com.m9d.sroom.youtube.vo.SearchInfo;
import com.m9d.sroom.youtube.vo.SearchItemInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Service
@Slf4j
public class LectureServiceV2 {

    private final PlaylistService playlistService;
    private final VideoService videoService;

    public LectureServiceV2(PlaylistService playlistService, VideoService videoService) {
        this.playlistService = playlistService;
        this.videoService = videoService;
    }

    public KeywordSearchResponse searchByKeyword(SearchInfo searchInfo, Set<String> enrolledLectureCodeSet) {
        String nextPageToken = Optional.of(searchInfo)
                .map(SearchInfo::getNextPageToken)
                .orElse(null);

        List<LectureResponse> contentDetailList = getLectureResponseListAsync(searchInfo).stream()
                .peek(lecture -> {
                    if (enrolledLectureCodeSet.contains(lecture.getLectureCode())) {
                        lecture.setEnrolled(true);
                    }
                })
                .collect(Collectors.toList());

        return KeywordSearchResponse.builder()
                .nextPageToken(nextPageToken)
                .resultPerPage(searchInfo.getResultPerPage())
                .lectures(contentDetailList)
                .build();

    }

    private List<LectureResponse> getLectureResponseListAsync(SearchInfo searchInfo) {
        List<CompletableFuture<LectureResponse>> futures = searchInfo
                .getSearchItemInfoList().stream()
                .map(item -> CompletableFuture.supplyAsync(() -> getLectureResponse(item)))
                .collect(Collectors.toList());
        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    private LectureResponse getLectureResponse(SearchItemInfo item) {
        if (item.isPlaylist()) {
            return new LectureResponse(item, playlistService.getRecentPlaylist(item.getCode()));
        } else {
            return new LectureResponse(item, videoService.getRecentVideo(item.getCode()));
        }
    }
}
