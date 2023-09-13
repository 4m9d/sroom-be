package com.m9d.sroom.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Lecture 테이블에서 리뷰를 위한 데이터")
public class LectureData {
    private Long lectureId;

    private Long courseId;

    private Long sourceId;

    private boolean isPlaylist;

    private int lectureIndex;

    private String channel;

    private Boolean isReviewed;
}
