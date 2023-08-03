package com.m9d.sroom.lecture.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseBrief {

    @Schema(description = "코스 ID",example = "44")
    private Long courseId;

    @Schema(description = "코스 제목", example = "침착맨 vs 주호민 산다면 누구 인생으로?")
    private String title;

    @Schema(description = "코스에 해당하는 영상 개수", example = "111")
    private int videoCount;
}
