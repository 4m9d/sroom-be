package com.m9d.sroom.lecture.controller;

import com.m9d.sroom.lecture.dto.response.KeywordSearchRes;
import com.m9d.sroom.lecture.service.LectureService;
import com.m9d.sroom.lecture.service.YoutubeService;
import io.swagger.annotations.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api")
@Slf4j
public class LectureController {

    private final LectureService lectureService;
    private final YoutubeService youtubeService;

    @GetMapping("/lectures")
    @ApiOperation(value = "강의 키워드 검색", notes = "키워드를 입력받아 유튜브 강의를 검색한다.", httpMethod = "GET", response = ResponseEntity.class, consumes = "application/json", tags = "강의 검색")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "검색할 키워드", required = true, dataType = "string", paramType = "query", example = "네트워크"),
            @ApiImplicitParam(name = "limit", value = "결과의 최대 개수", required = false, dataType = "int", paramType = "query", defaultValue = "10", example = "5"),
            @ApiImplicitParam(name = "nextPageToken", value = "다음 페이지 토큰", required = false, dataType = "string", paramType = "query", example = "QAUQAA"),
            @ApiImplicitParam(name = "prevPageToken", value = "이전 페이지 토큰", required = false, dataType = "string", paramType = "query", example = "CAUQAA")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "성공적으로 검색 결과를 반환하였습니다.", response = KeywordSearchRes.class),
            @ApiResponse(code = 400, message = "요청이 유효하지 않습니다 - 필수 파라미터 { keyword } 가 누락되었습니다.")
    })
    public ResponseEntity<KeywordSearchRes> getLecturesByKeyword(@RequestParam(name = "keyword", required = true) String keyword,
                                                                 @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
                                                                 @RequestParam(name = "nextPageToken", required = false) String nextPageToken,
                                                                 @RequestParam(name = "prevPageToken", required = false) String prevPageToken) throws Exception {
        KeywordSearchRes keywordSearchRes = youtubeService.searchByKeyword(keyword, limit, nextPageToken, prevPageToken);
        return ResponseEntity.ok(keywordSearchRes);
    }
}
