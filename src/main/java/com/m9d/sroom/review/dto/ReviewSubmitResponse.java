package com.m9d.sroom.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "리뷰 작성 API Response")
@Builder
@Data
public class ReviewSubmitResponse {

    private Long lectureId;

    private Long reviewId;

    private Integer submittedRating;

    private String reviewContent;
}
