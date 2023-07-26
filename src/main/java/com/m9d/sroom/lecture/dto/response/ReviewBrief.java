package com.m9d.sroom.lecture.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "후기 정보")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewBrief {

    @Schema(description = "순서", example = "1")
    private int index;

    @Schema(description = "후기 내용", example = "발음을 못알아먹것서요~")
    private String reviewContent;

    @Schema(description = "별점", example = "2")
    private int submittedRating;

    @Schema(description = "후기 남긴 사람 이름", example = "user_318593")
    private String reviewerName;

    @Schema(description = "후기 생성 날짜", example = "2022-01-34")
    private String publishedAt;
}
