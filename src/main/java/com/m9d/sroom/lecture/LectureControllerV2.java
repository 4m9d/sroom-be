package com.m9d.sroom.lecture;


import com.m9d.sroom.course.CourseService;
import com.m9d.sroom.course.CourseServiceV2;
import com.m9d.sroom.lecture.dto.request.KeywordSearchParam;
import com.m9d.sroom.lecture.dto.request.LectureDetailParam;
import com.m9d.sroom.lecture.dto.request.LectureTimeRecord;
import com.m9d.sroom.lecture.dto.response.*;
import com.m9d.sroom.playlist.PlaylistService;
import com.m9d.sroom.util.JwtUtil;
import com.m9d.sroom.util.ValidateUtil;
import com.m9d.sroom.util.annotation.Auth;
import com.m9d.sroom.video.VideoService;
import com.m9d.sroom.youtube.YoutubeServiceV2;
import com.m9d.sroom.youtube.vo.SearchInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    private final CourseServiceV2 courseService;
    private final JwtUtil jwtUtil;

    public LectureControllerV2(LectureServiceV2 lectureService, YoutubeServiceV2 youtubeService,
                               PlaylistService playlistService, VideoService videoService,
                               CourseServiceV2 courseService, JwtUtil jwtUtil) {
        this.lectureService = lectureService;
        this.youtubeService = youtubeService;
        this.playlistService = playlistService;
        this.videoService = videoService;
        this.courseService = courseService;
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

        Set<String> enrolledLectureSet = new HashSet<>(playlistService.getEnrolledCodeSet(memberId));
        enrolledLectureSet.addAll(videoService.getEnrolledCodeSet(memberId));

        return lectureService.searchByKeyword(searchInfo, enrolledLectureSet);
    }

    @Auth
    @GetMapping("/{lectureCode}")
    @Tag(name = "강의 검색")
    @Operation(summary = "강의 상세 정보 조회", description = "강의 ID를 이용하여 강의의 상세 정보를 조회한다.")
    @Parameters({
            @Parameter(in = ParameterIn.PATH, name = "lectureCode", description = "강의 코드", required = true, example = "OEV8gMkCHXQ"),
            @Parameter(in = ParameterIn.QUERY, name = "index_only", description = "목차만 응답 여부", required = false, example = "false"),
            @Parameter(in = ParameterIn.QUERY, name = "review_only", description = "후기만 응답 여부", required = false, example = "false"),
            @Parameter(in = ParameterIn.QUERY, name = "index_limit", description = "결과의 최대 개수", required = false, example = "50"),
            @Parameter(in = ParameterIn.QUERY, name = "review_limit", description = "후기의 최대 개수", required = false, example = "10"),
            @Parameter(in = ParameterIn.QUERY, name = "index_next_token", description = "목차 다음 페이지 토큰", required = false, example = "EAAaBlBUOkNESQ")
    })
    @ApiResponse(responseCode = "200", description = "성공적으로 강의 상세 정보를 반환하였습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(oneOf = {PlaylistDetail.class, VideoDetail.class, IndexInfo.class}))})
    public Object getLectureDetail(@PathVariable(name = "lectureCode") String lectureCode, @ModelAttribute LectureDetailParam lectureDetailParam) {
        Long memberId = jwtUtil.getMemberIdFromRequest();

        Set<String> enrolledLectureSet = new HashSet<>(playlistService.getEnrolledCodeSet(memberId));
        enrolledLectureSet.addAll(videoService.getEnrolledCodeSet(memberId));

        if (ValidateUtil.checkIfPlaylist(lectureCode) && !lectureDetailParam.isIndexOnly()) {
            return lectureService.getContentDetail(playlistService.getRecentPlaylist(lectureCode),
                    lectureDetailParam, enrolledLectureSet, courseService.getCourseBriedList(memberId));
        } else if (ValidateUtil.checkIfPlaylist(lectureCode)) {
            return lectureService.getContentDetail(playlistService.getRecentPlaylistWithItemList(lectureCode),
                    lectureDetailParam, enrolledLectureSet, courseService.getCourseBriedList(memberId));
        } else {
            return lectureService.getContentDetail(videoService.getRecentVideo(lectureCode),
                    lectureDetailParam, enrolledLectureSet, courseService.getCourseBriedList(memberId));
        }
    }
}
