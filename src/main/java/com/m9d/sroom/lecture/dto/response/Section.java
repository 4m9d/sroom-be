package com.m9d.sroom.lecture.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Schema(description = "강의 일정의 주별 정보")
@Data
@Builder
public class Section {

    @Schema(description = "주차 번호", example = "1")
    private int section;

    @Schema(description = "해당 주차의 총 강의 기간 (분 단위)", example = "120")
    private int weekDuration;

    @JsonProperty("is_completed")
    @Schema(description = "해당 주차의 강의가 완료되었는지 여부", example = "false")
    private boolean completed;

    @Schema(description = "해당 주차에서 현재 시청 완료한 강의 기간 (분 단위)", example = "60")
    private int currentWeekDuration;

    @Schema(description = "해당 주차의 비디오 목록")
    private List<VideoBrief> videos;
}