package com.m9d.sroom.recommendation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.*;

@Builder
@Data
@Schema(description = "강의 추천 리스트")
public class Recommendations {

    @Schema(description = "추천 강의 리스트")
    private List<RecommendLecture> generalRecommendations;

    @Schema(description = "추천 강의 리스트")
    private List<RecommendLecture> channelRecommendations;
}
