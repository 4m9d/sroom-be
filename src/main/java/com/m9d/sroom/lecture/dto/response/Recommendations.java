package com.m9d.sroom.lecture.dto.response;

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
}
