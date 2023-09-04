package com.m9d.sroom.lecture.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "해당 강의에 대한 수강정보")
@Data
@Builder
public class LectureStatus {

    private Long courseVideoId;

    private int viewDuration;

    @JsonProperty("is_completed")
    private boolean complete;
}
