package com.m9d.sroom.lecture.controller;

import com.m9d.sroom.lecture.dto.response.KeywordSearchRes;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Slf4j
public class LectureController {

    private final LectureService lectureService;
    private final YoutubeService youtubeService;

    @GetMapping("/lectures")
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
}
