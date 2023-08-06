package com.m9d.sroom.lecture.controller;

import com.m9d.sroom.lecture.dto.request.KeywordSearchParam;
import com.m9d.sroom.lecture.dto.request.LectureDetailParam;
import com.m9d.sroom.lecture.dto.response.*;
import com.m9d.sroom.lecture.service.LectureServiceV2;
import com.m9d.sroom.util.JwtUtil;
import com.m9d.sroom.util.annotation.Auth;
import com.m9d.sroom.util.youtube.YoutubeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/lectures")
@Slf4j
public class LectureController {

    //private final LectureService lectureService;
    private final LectureServiceV2 lectureServiceV2;
    private final JwtUtil jwtUtil;
    private final YoutubeUtil youtubeUtil;

    @Auth
    @GetMapping("")
    @Tag(name = "강의 검색")
    @Operation(summary = "강의 키워드 검색", description = "키워드를 입력받아 유튜브 강의를 검색한다.")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "keyword", description = "검색할 키워드", required = true, example = "네트워크"),
            @Parameter(in = ParameterIn.QUERY, name = "limit", description = "결과의 최대 개수", required = false, example = "5"),
            @Parameter(in = ParameterIn.QUERY, name = "filter", description = "검색 종류 필터, all, playlist, video", required = false, example = "all"),
            @Parameter(in = ParameterIn.QUERY, name = "nextPageToken", description = "다음 페이지 토큰", required = false, example = "QAUQAA"),
            @Parameter(in = ParameterIn.QUERY, name = "prevPageToken", description = "이전 페이지 토큰", required = false, example = "CAUQAA")
    })
    @ApiResponse(responseCode = "200", description = "성공적으로 검색 결과를 반환하였습니다.", content = @Content(schema = @Schema(implementation = KeywordSearch.class)))
    public KeywordSearch getLecturesByKeyword(@Valid @ModelAttribute KeywordSearchParam keywordSearchParam) {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        KeywordSearch keywordSearch = lectureServiceV2.searchByKeyword(memberId, keywordSearchParam);
        return keywordSearch;
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
    public ResponseEntity<?> getLectureDetail(@PathVariable(name = "lectureCode") String lectureCode, @ModelAttribute LectureDetailParam lectureDetailParam) {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        boolean isPlaylist = youtubeUtil.checkIfPlaylist(lectureCode);

        ResponseEntity<?> lectureDetail = lectureServiceV2.getLectureDetail(memberId, isPlaylist, lectureCode, lectureDetailParam);
        return lectureDetail;
    }

    @Auth
    @GetMapping("/recommendations")
    @Tag(name = "강의 검색")
    @Operation(summary = "강의 추천", description = "유저 ID를 받아 적당한 강의를 추천한다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 추천 결과를 반환하였습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Recommendations.class))})
    public Recommendations getRecommendations() {
        return null;
    }


    @Auth
    @GetMapping("/{courseId}")
    @Tag(name = "강의 수강")
    @Operation(summary = "수강페이지 코스정보", description = "코스 ID를 받아 해당 코스 정보와 수강할 영상 리스트를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 수강 정보를 반환하였습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CourseDetail.class))})
    public CourseDetail getCourseDetail(@PathVariable(name = "courseId") Long courseId) {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        return lectureServiceV2.getCourseDetail(memberId, courseId);
    }
}
