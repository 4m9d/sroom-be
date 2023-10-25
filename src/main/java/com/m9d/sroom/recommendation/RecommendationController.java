package com.m9d.sroom.recommendation;

import com.m9d.sroom.recommendation.dto.Recommendations;
import com.m9d.sroom.util.JwtUtil;
import com.m9d.sroom.util.annotation.Auth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lectures")
@Slf4j
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final JwtUtil jwtUtil;

    public RecommendationController(RecommendationService recommendationService, JwtUtil jwtUtil) {
        this.recommendationService = recommendationService;
        this.jwtUtil = jwtUtil;
    }
    @Auth
    @GetMapping("/recommendations")
    @Tag(name = "강의 검색")
    @Operation(summary = "강의 추천", description = "유저 ID를 받아 적당한 강의를 추천한다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 추천 결과를 반환하였습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Recommendations.class))})
    public Recommendations getRecommendations() {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        return recommendationService.getRecommendations(memberId);
    }
}
