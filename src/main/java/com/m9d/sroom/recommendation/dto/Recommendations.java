package com.m9d.sroom.recommendation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.*;

@Builder
@Data
@Schema(description = "강의 추천 리스트")
public class Recommendations {
    @Schema(description = "스룸 전체 강의 추천 리스트")
    private List<RecommendLecture> generalRecommendations;

    @Schema(description = "유저 시청 채널기반 추천 리스트")
    private List<RecommendLecture> channelRecommendations;

    @Schema(description = "시사/사회 추천 리스ㅌ")
    private List<RecommendLecture> societyRecommendations;

    @Schema(description = "과학 추천 리스트")
    private List<RecommendLecture> scienceRecommendations;

    @Schema(description = "경제 추천 리스트")
    private List<RecommendLecture> economicRecommendations;

    @Schema(description = "IT 추천 리스트")
    private List<RecommendLecture> techRecommendations;
}
