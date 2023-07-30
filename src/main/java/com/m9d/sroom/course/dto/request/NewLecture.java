package com.m9d.sroom.course.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "새로운 강의 정보")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewLecture {

    @Schema(description = "강의 코드", example = "PL0d8NnikouEWcF1jJueLdjRIC4HsUlULi")
    @NotNull
    private String lectureCode;

    @Schema(description = "일일 목표 시간", example = "120")
    private int dailyTargetTime;

    @Schema(description = "강의 스케줄", example = "[1,2,3,4,5]")
    private List<Integer> scheduling;

    @Schema(description = "예상 종료 시간", example = "2023-12-31")
    private String expectedEndTime;


}
