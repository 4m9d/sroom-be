package com.m9d.sroom.lecture;


import com.m9d.sroom.lecture.dto.request.KeywordSearchParam;
import com.m9d.sroom.lecture.dto.response.KeywordSearchResponse;
import com.m9d.sroom.playlist.PlaylistService;
import com.m9d.sroom.util.JwtUtil;
import com.m9d.sroom.util.annotation.Auth;
import com.m9d.sroom.video.VideoService;
import com.m9d.sroom.youtube.YoutubeServiceV2;
import com.m9d.sroom.youtube.vo.SearchInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/lectures")
@Slf4j
public class LectureControllerV2 {

    private final LectureServiceV2 lectureService;
    private final YoutubeServiceV2 youtubeService;
    private final PlaylistService playlistService;
    private final VideoService videoService;
    private final JwtUtil jwtUtil;

    public LectureControllerV2(LectureServiceV2 lectureService, YoutubeServiceV2 youtubeService, PlaylistService playlistService, VideoService videoService, JwtUtil jwtUtil) {
        this.lectureService = lectureService;
        this.youtubeService = youtubeService;
        this.playlistService = playlistService;
        this.videoService = videoService;
        this.jwtUtil = jwtUtil;
    }

    @Auth
    @GetMapping("")
    @Tag(name = "강의 검색")
    @Operation(summary = "강의 키워드 검색", description = "키워드를 입력받아 유튜브 강의를 검색한다.")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "keyword", description = "검색할 키워드", required = true, example = "네트워크"),
            @Parameter(in = ParameterIn.QUERY, name = "limit", description = "결과의 최대 개수", example = "5"),
            @Parameter(in = ParameterIn.QUERY, name = "filter", description = "검색 종류 필터, all, playlist, video", example = "all"),
            @Parameter(in = ParameterIn.QUERY, name = "nextPageToken", description = "다음 페이지 토큰", example = "QAUQAA"),
            @Parameter(in = ParameterIn.QUERY, name = "prevPageToken", description = "이전 페이지 토큰", example = "CAUQAA")
    })
    public KeywordSearchResponse searchByKeyword(@Valid @ModelAttribute KeywordSearchParam keywordSearchParam) {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        SearchInfo searchInfo = youtubeService.getSearchInfo(keywordSearchParam.getKeyword(),
                keywordSearchParam.getNextPageToken(), keywordSearchParam.getLimit(), keywordSearchParam.getFilter());

        Set<String> enrolledLectureSet = new HashSet<>();
        enrolledLectureSet.addAll(playlistService.getEnrolledCodeSet(memberId));
        enrolledLectureSet.addAll(videoService.getEnrolledCodeSet(memberId));

        return lectureService.searchByKeyword(searchInfo, enrolledLectureSet);
    }
}
