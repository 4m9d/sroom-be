package com.m9d.sroom.lecture.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "후기 정보")
@Data
public class ReviewBrief {

    @Schema(description = "순서", example = "1")
    private int index;

    @Schema(description = "후기 내용", example = "발음을 못알아먹것서요~")
    private String reviewContent;

    @Schema(description = "별점", example = "2")
    private int submittedRating;

    @Builder
    public ReviewBrief(int index, String reviewContent, int submittedRating) {
        this.index = index;
        this.reviewContent = reviewContent;
        this.submittedRating = submittedRating;
    }
}
