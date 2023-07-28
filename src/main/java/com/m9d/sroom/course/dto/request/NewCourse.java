package com.m9d.sroom.course.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "새로운 강의 정보")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCourse {

    @Schema(description = "강의 코드", example = "PL0d8NnikouEWcF1jJueLdjRIC4HsUlULi")
    private String lectureCode;

    @Schema(description = "일일 목표 시간", example = "120")
    private int dailyTargetTime;

    @Schema(description = "강의 스케줄", example = "[1,2,3,4,5]")
    private List<Integer> scheduling;

    @Schema(description = "예상 종료 시간", example = "2023-12-31")
    private String expectedEndTime;

    @Schema(description = "강의 제목", example = "데이터 과학 기초")
    private String title;

    @Schema(description = "강의 제공 채널", example = "따라하면서 배우는 IT")
    private String channel;

    @Schema(description = "강의 설명", example = "이 강의는 데이터 과학의 기초에 대해 다룹니다.")
    private String description;

    @Schema(description = "강의 시간", example = "300")
    private String duration;

    @Schema(description = "강의 썸네일 URL", example = "https://i.ytimg.com/vi/Av9UFzl_wis/hqdefault.jpg")
    private String thumbnail;

    @Schema(description = "강의 인덱스 카운트", example = "0")
    private int indexCount = 0;

}
