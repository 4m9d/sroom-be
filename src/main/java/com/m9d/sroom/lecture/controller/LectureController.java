package com.m9d.sroom.lecture.controller;

import com.m9d.sroom.lecture.dto.response.ReviewBrief;
import com.m9d.sroom.lecture.dto.response.*;
import com.m9d.sroom.lecture.exception.TwoOnlyParamTrueException;
import com.m9d.sroom.lecture.exception.VideoIndexParamException;
import com.m9d.sroom.lecture.service.LectureService;
import com.m9d.sroom.lecture.service.YoutubeService;
import com.m9d.sroom.util.JwtUtil;
import com.m9d.sroom.util.annotation.Auth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 검색 결과를 반환하였습니다.", content = @Content(schema = @Schema(implementation = KeywordSearch.class))),
            @ApiResponse(responseCode = "400", description = "keyword가 입력되지 않았습니다.", content = @Content)
    })
    public ResponseEntity<KeywordSearch> getLecturesByKeyword(@RequestParam(name = "keyword", required = true) String keyword,
                                                              @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
                                                              @RequestParam(name = "filter", required = false, defaultValue = "all") String filter,
                                                              @RequestParam(name = "next_page_token", required = false) String nextPageToken,
                                                              @RequestParam(name = "prev_page_token", required = false) String prevPageToken) throws Exception {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        KeywordSearch keywordSearch = lectureService.searchByKeyword(memberId, keyword, limit, filter, nextPageToken, prevPageToken);
        return ResponseEntity.ok(keywordSearch);
    }


    @Auth
    @GetMapping("/{lectureCode}")
    @Tag(name = "강의 검색")
    @Operation(summary = "강의 상세 정보 조회", description = "강의 ID를 이용하여 강의의 상세 정보를 조회한다.")
    @Parameters({
            @Parameter(in = ParameterIn.PATH, name = "lectureCode", description = "강의 코드", required = true, example = "OEV8gMkCHXQ"),
            @Parameter(in = ParameterIn.QUERY, name = "is_playlist", description = "플레이리스트 여부", required = true, example = "false"),
            @Parameter(in = ParameterIn.QUERY, name = "index_only", description = "목차만 응답 여부", required = false, example = "false"),
            @Parameter(in = ParameterIn.QUERY, name = "review_only", description = "후기만 응답 여부", required = false, example = "false"),
            @Parameter(in = ParameterIn.QUERY, name = "index_limit", description = "결과의 최대 개수", required = false, example = "50"),
            @Parameter(in = ParameterIn.QUERY, name = "review_limit", description = "후기의 최대 개수", required = false, example = "10")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 강의 상세 정보를 반환하였습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(oneOf = {PlaylistDetail.class, VideoDetail.class, IndexInfo.class}))}),
            @ApiResponse(responseCode = "400", description = "필수 파라미터인 'is_playlist'(boolean)가 누락되었습니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "입력한 lectureCode에 해당하는 강의가 없습니다.", content = @Content)
    })
    public ResponseEntity<?> getLectureDetail(@PathVariable(name = "lectureCode", required = true) String lectureCode,
                                              @RequestParam(name = "is_playlist", required = true) boolean isPlaylist,
                                              @RequestParam(name = "index_only", required = false, defaultValue = "false") boolean indexOnly,
                                              @RequestParam(name = "index_limit", required = false, defaultValue = "50") int indexLimit,
                                              @RequestParam(name = "index_next_token", required = false) String indexNextToken,
                                              @RequestParam(name = "review_only", required = false, defaultValue = "false") boolean reviewOnly,
                                              @RequestParam(name = "review_offset", required = false, defaultValue = "0") int reviewOffset,
                                              @RequestParam(name = "review_limit", required = false, defaultValue = "10") int reviewLimit) throws Exception {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        if (indexOnly && reviewOnly) {
            throw new TwoOnlyParamTrueException();
        }
        if (!isPlaylist && indexOnly) {
            throw new VideoIndexParamException();
        }
        if (indexOnly) {
            IndexInfo indexInfo = lectureService.getPlaylistItems(lectureCode, indexNextToken, indexLimit);
            return ResponseEntity.ok(indexInfo);
        }
        if (reviewOnly) {
            List<ReviewBrief> reviewBriefList = lectureService.getReviewBriefList(lectureCode, reviewOffset, reviewLimit);
            return ResponseEntity.ok(reviewBriefList);
        }
        if (isPlaylist) {
            PlaylistDetail playlistDetail = lectureService.getPlaylistDetail(lectureCode, indexNextToken, reviewLimit);
            return ResponseEntity.ok(playlistDetail);
        }
        VideoDetail videoDetail = lectureService.getVideoDetail(lectureCode, reviewLimit);
        return ResponseEntity.ok(videoDetail);
    }
}
