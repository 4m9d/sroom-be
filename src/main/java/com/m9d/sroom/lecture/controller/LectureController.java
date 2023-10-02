package com.m9d.sroom.lecture.controller;

import com.m9d.sroom.lecture.dto.request.KeywordSearchParam;
import com.m9d.sroom.lecture.dto.request.LectureDetailParam;
import com.m9d.sroom.lecture.dto.request.LectureTimeRecord;
import com.m9d.sroom.lecture.dto.response.*;
import com.m9d.sroom.lecture.service.LectureService;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/lectures")
@Slf4j
public class LectureController {

    private final LectureService lectureService;
    private final JwtUtil jwtUtil;

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
        return lectureService.searchByKeyword(memberId, keywordSearchParam);
    }

    @Auth
    @GetMapping("/recommendations")
    @Tag(name = "강의 검색")
    @Operation(summary = "강의 추천", description = "유저 ID를 받아 적당한 강의를 추천한다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 추천 결과를 반환하였습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Recommendations.class))})
    public Recommendations getRecommendations() {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        log.info("recommend start");
        return lectureService.getRecommendations(memberId);
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
        boolean isPlaylist = YoutubeUtil.checkIfPlaylist(lectureCode);
        return lectureService.getLectureDetail(memberId, isPlaylist, lectureCode, lectureDetailParam);
    }

    @Auth
    @PutMapping("/{courseVideoId}/time")
    @Tag(name = "강의 수강")
    @Operation(summary = "시청중인 강의 학습시간 저장하기", description = "duration을 입력받아 업데이트하고, 70%가 넘었다면 수강완료 처리한다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 학습시간을 저장 하였습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LectureStatus.class))})
    public LectureStatus updateLectureTime(@PathVariable(name = "courseVideoId") Long courseVideoId, @Valid @RequestBody LectureTimeRecord record, @RequestParam(name = "isCompletedManually", required = false, defaultValue = "false") boolean isCompletedManually) {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        return lectureService.updateLectureTime(memberId, courseVideoId, record, isCompletedManually);
    }

}
