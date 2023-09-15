package com.m9d.sroom.review.controller;

import com.m9d.sroom.review.dto.LectureBriefList4Review;
import com.m9d.sroom.review.dto.ReviewSubmitRequest;
import com.m9d.sroom.review.dto.ReviewSubmitResponse;
import com.m9d.sroom.review.service.ReviewService;
import com.m9d.sroom.util.JwtUtil;
import com.m9d.sroom.util.annotation.Auth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;
    private final JwtUtil jwtUtil;

    @Auth
    @GetMapping("/courses/{courseId}")
    @Tag(name = "리뷰 평점")
    @Operation(summary = "리뷰 평점 강의 리스트 조회", description = "리뷰 평점을 위한 코스 내 강의 리스트 조회")
    @ApiResponse(responseCode = "200", description = "성공적으로 강의 리스트를 불러왔습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LectureBriefList4Review.class))})
    public LectureBriefList4Review getLectureList(@PathVariable(name = "courseId") Long courseId) {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        LectureBriefList4Review lectureList4Review = reviewService.getLectureList(memberId, courseId);
        return lectureList4Review;
    }

    @Auth
    @PostMapping("/lectures/{lectureId}")
    @Tag(name = "리뷰 평점")
    @Operation(summary = "리뷰 평점 작성", description = "리뷰 평점 작성 및 제출")
    @ApiResponse(responseCode = "200", description = "성공적으로 작성 완료되었습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ReviewSubmitResponse.class))})
    public ReviewSubmitResponse postReview(@PathVariable(name = "lectureId") Long lectureId, @Valid @RequestBody ReviewSubmitRequest reviewSubmitRequest) {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        ReviewSubmitResponse reviewSubmitResponse = reviewService.submitReview(memberId, lectureId, reviewSubmitRequest);
        return reviewSubmitResponse;
    }
}
