package com.m9d.sroom.lecture;

import com.m9d.sroom.common.vo.Content;
import com.m9d.sroom.common.vo.Playlist;
import com.m9d.sroom.common.vo.PlaylistWithItemList;
import com.m9d.sroom.common.vo.Video;
import com.m9d.sroom.lecture.dto.request.LectureDetailParam;
import com.m9d.sroom.lecture.dto.response.*;
import com.m9d.sroom.lecture.exception.TwoOnlyParamTrueException;
import com.m9d.sroom.lecture.exception.VideoIndexParamException;
import com.m9d.sroom.playlist.PlaylistService;
import com.m9d.sroom.review.service.ReviewService;
import com.m9d.sroom.video.VideoService;
import com.m9d.sroom.youtube.vo.SearchInfo;
import com.m9d.sroom.youtube.vo.SearchItemInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.m9d.sroom.lecture.constant.LectureConstant.DEFAULT_REVIEW_OFFSET;


@Service
@Slf4j
public class LectureServiceV2 {

    private final PlaylistService playlistService;
    private final VideoService videoService;

    private final ReviewService reviewService;

    public LectureServiceV2(PlaylistService playlistService, VideoService videoService, ReviewService reviewService) {
        this.playlistService = playlistService;
        this.videoService = videoService;
        this.reviewService = reviewService;
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

    @Transactional
    public Object getContentDetail(Content content, LectureDetailParam lectureDetailParam,
                                   Set<String> enrolledLectureSet, List<CourseBrief> courseBriefList) {
        validateLectureDetailParam(content.isPlaylist(), lectureDetailParam);

        if (!content.isPlaylist() && !lectureDetailParam.isReviewOnly()) {
            return new VideoDetail((Video) content, enrolledLectureSet, courseBriefList, reviewService
                    .getReviewInfo(content.getCode(), DEFAULT_REVIEW_OFFSET, lectureDetailParam.getReviewLimit()));
        }

        if (content.isPlaylist() && !lectureDetailParam.isIndexOnly() && !lectureDetailParam.isReviewOnly()) {
            return new PlaylistDetail((Playlist) content, enrolledLectureSet, courseBriefList, reviewService
                    .getReviewInfo(content.getCode(), DEFAULT_REVIEW_OFFSET, lectureDetailParam.getReviewLimit()));
        }

        if (lectureDetailParam.isIndex_only()) {
            return new IndexInfo((PlaylistWithItemList) content);
        }

        return reviewService.getReviewInfo(content.getCode(), lectureDetailParam.getReviewOffset(),
                lectureDetailParam.getReviewLimit());

    }

    private void validateLectureDetailParam(boolean isPlaylist, LectureDetailParam lectureDetailParam) {
        if (lectureDetailParam.isIndexOnly() && lectureDetailParam.isReviewOnly()) {
            throw new TwoOnlyParamTrueException();
        }
        if (!isPlaylist && lectureDetailParam.isIndexOnly()) {
            throw new VideoIndexParamException();
        }
    }
}
