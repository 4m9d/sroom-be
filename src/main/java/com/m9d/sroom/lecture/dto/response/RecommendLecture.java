package com.m9d.sroom.lecture.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Schema(description = "추천 강의 정보")
public class RecommendLecture {

    @Schema(description = "강의 제목")
    private String lectureTitle;

    @Schema(description = "강의 설명")
    private String description;

    @Schema(description = "채널")
    private String channel;

    @Schema(description = "강의 ID")
    private String lectureCode;

    @JsonProperty("is_playlist")
    @Schema(description = "플레이리스트 여부")
    private boolean isPlaylist;

    @Schema(description = "강의 평점")
    private double rating;

    @Schema(description = "리뷰 개수")
    private int reviewCount;

    @Schema(description = "썸네일")
    private String thumbnail;
}
