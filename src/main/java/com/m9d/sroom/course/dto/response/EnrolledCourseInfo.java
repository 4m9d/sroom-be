package com.m9d.sroom.course.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;


@Schema(description = "등록된 코스 정보")
@Data
@Builder
public class EnrolledCourseInfo {

    @Schema(description = "등록된 코스 번호", example = "12")
    private Long courseId;

    @Schema(description = "등록된 강의 번호", example = "44")
    private Long lectureId;
}
