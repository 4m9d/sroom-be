package com.m9d.sroom.lecture.controller;

import com.m9d.sroom.lecture.dto.response.KeywordSearchRes;
import com.m9d.sroom.lecture.dto.response.VideoDetail;
import com.m9d.sroom.lecture.service.LectureService;
import com.m9d.sroom.lecture.service.YoutubeService;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/lectures")
@Slf4j
public class LectureController {

    private final LectureService lectureService;
    private final YoutubeService youtubeService;

    @GetMapping("")
    @Tag(name = "강의 검")
    @Operation(summary = "강의 키워드 검색", description = "키워드를 입력받아 유튜브 강의를 검색한다.")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "keyword", description = "검색할 키워드", required = true, example = "네트워크"),
            @Parameter(in = ParameterIn.QUERY, name = "limit", description = "결과의 최대 개수", required = false, example = "5"),
            @Parameter(in = ParameterIn.QUERY, name = "nextPageToken", description = "다음 페이지 토큰", required = false, example = "QAUQAA"),
            @Parameter(in = ParameterIn.QUERY, name = "prevPageToken", description = "이전 페이지 토큰", required = false, example = "CAUQAA")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 검색 결과를 반환하였습니다.", content = @Content(schema = @Schema(implementation = KeywordSearchRes.class))),
            @ApiResponse(responseCode = "400", description = "keyword가 입력되지 않았습니다.", content = @Content)
    })
    public ResponseEntity<KeywordSearchRes> getLecturesByKeyword(@RequestParam(name = "keyword", required = true) String keyword,
                                                                 @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
                                                                 @RequestParam(name = "nextPageToken", required = false) String nextPageToken,
                                                                 @RequestParam(name = "prevPageToken", required = false) String prevPageToken) throws Exception {
        KeywordSearchRes keywordSearchRes = youtubeService.searchByKeyword(keyword, limit, nextPageToken, prevPageToken);
        return ResponseEntity.ok(keywordSearchRes);
    }


    @GetMapping("/{lectureId}")
    @Tag(name = "강의 상세 정보")
    @Operation(summary = "강의 상세 정보 조회", description = "강의 ID를 이용하여 강의의 상세 정보를 조회한다.")
    @Parameters({
            @Parameter(in = ParameterIn.PATH, name = "lectureId", description = "강의 ID", required = true, example = "OEV8gMkCHXQ"),
            @Parameter(in = ParameterIn.QUERY, name = "is_playlist", description = "플레이리스트 여부", required = true, example = "false"),
            @Parameter(in = ParameterIn.QUERY, name = "index_limit", description = "결과의 최대 개수", required = false, example = "10"),
            @Parameter(in = ParameterIn.QUERY, name = "review_limit", description = "리뷰의 최대 개수", required = false, example = "10")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 강의 상세 정보를 반환하였습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VideoDetail.class))}),
            @ApiResponse(responseCode = "400", description = "필수 파라미터인 'is_playlist'(boolean)가 누락되었습니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "입력한 lectureId에 해당하는 강의가 없습니다.", content = @Content)
    })
    public ResponseEntity<?> getLectureDetail(@PathVariable(name = "lectureId", required = true) String lectureId,
                                              @RequestParam(name = "is_playlist", required = true) boolean isPlaylist,
                                              @RequestParam(name = "index_limit", required = false, defaultValue = "10") int indexLimit,
                                              @RequestParam(name = "review_limit", required = false, defaultValue = "10") int reviewLimit) throws Exception {
//        if(isPlaylist){
//            PlaylistDetail playlistDetail = youtubeService.getPlaylistDetail(lectureId,indexLimit,reviewLimit);
//            return ResponseEntity.ok(playlistDetail);
//        }
//        else {
//            VideoDetail videoDetail = youtubeService.getVideoDetail(lectureId, indexLimit, reviewLimit);
//            return ResponseEntity.ok(videoDetail);
//        }

        VideoDetail videoDetail = youtubeService.getVideoDetail(lectureId, indexLimit, reviewLimit);
        return ResponseEntity.ok(videoDetail);
    }
}
