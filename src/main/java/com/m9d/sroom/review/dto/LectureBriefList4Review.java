package com.m9d.sroom.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Schema(description = "강의 리스트")
@Data
@Builder
public class LectureBriefList4Review {

    @Schema(description = "강의 리스트")
    private List<LectureBrief4Review> lectures;
}
