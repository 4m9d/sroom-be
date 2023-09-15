package com.m9d.sroom.global.mapper;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "각 강의에 대한 리뷰 데이터")
public class Review {

    private Long reviewId;

    private String sourceCode;

    private Long memberId;

    private Long lectureId;

    private String content;

    private Integer submittedRating;

    private String submittedDate;
}
