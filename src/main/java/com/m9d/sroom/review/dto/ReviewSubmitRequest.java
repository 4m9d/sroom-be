package com.m9d.sroom.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "리뷰 작성 API 요청 객체")
public class ReviewSubmitRequest {
    private Integer submittedRating;

    private String reviewContent;
}
