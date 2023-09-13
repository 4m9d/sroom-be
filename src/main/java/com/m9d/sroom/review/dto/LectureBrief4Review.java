package com.m9d.sroom.review.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Schema(description = "리뷰 작성 페이지 강의 정보")
public class LectureBrief4Review {

    @Schema(description = "강의 인덱스")
    private int index;

    @Schema(description = "강의 ID")
    private Long lectureId;

    @Schema(description = "강의 제목")
    private String title;

    @Schema(description = "강의 썸네일")
    private String thumbnail;

    @Schema(description = "채널명")
    private String channel;

    @Schema(description = "플레이리스트 여부")
    @JsonProperty("is_playlist")
    private boolean isPlaylist;

    @Schema(description = "시청 시간")
    private int viewDuration;

    @Schema(description = "총 시간")
    private int lectureDuration;

    @Schema(description = "수강 완료 영상 갯수")
    private int completedVideoCount;

    @Schema(description = "총 영상 갯수")
    private int totalVideoCount;

    @Schema(description = "진행률")
    private int progress;

    @Schema(description = "리뷰 작성 날짜")
    private String submittedAt;

    @Schema(description = "리뷰 내용")
    private String content;

    @Schema(description = "유저 평점")
    private Integer rating;

    @Schema(description = "리뷰 작성 가능 여부")
    @JsonProperty("is_review_allowed")
    private boolean isReviewAllowed;
}
