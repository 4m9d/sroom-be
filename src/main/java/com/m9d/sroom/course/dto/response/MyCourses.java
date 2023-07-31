package com.m9d.sroom.course.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@Schema(description = "내 강의실 데이터")
public class MyCourses {

    @Schema(description = "미 수강완료 강의 갯수")
    private int unfinishedCourse;

    @Schema(description = "완강률")
    private int completionRate;

    @Schema(description = "강의 코스 데이터 리스트")
    private List<CourseInfo> courses;
}
