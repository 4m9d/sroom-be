package com.m9d.sroom.lecture.controller;

import com.m9d.sroom.lecture.dto.response.KeywordSearchRes;
import com.m9d.sroom.lecture.service.LectureService;
import com.m9d.sroom.lecture.service.YoutubeService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("lectures")
public class LectureController {

    private final LectureService lectureService;
    private final YoutubeService youtubeService;

    @GetMapping("")
    public ResponseEntity<KeywordSearchRes> getLecturesByKeyword(@RequestParam(name = "keyword", required = true) String keyword,
                                                                 @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
                                                                 @RequestParam(name = "nextPageToken", required = false) String nextPageToken,
                                                                 @RequestParam(name = "prevPageToken", required = false) String prevPageToken) throws Exception{
        KeywordSearchRes keywordSearchRes = youtubeService.searchByKeyword(keyword, limit, nextPageToken, prevPageToken);
        return ResponseEntity.ok(keywordSearchRes);
    }

    @GetMapping("a")
    public ResponseEntity<Void> asdf(){
        return ResponseEntity.ok().build();
    }

}
