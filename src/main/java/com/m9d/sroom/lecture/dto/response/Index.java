package com.m9d.sroom.lecture.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "재생목록 목차 정보")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Index {

    @Schema(description = "목차 번호", example = "1")
    private int index;

    @Schema(description = "강의 썸네일", example = "https://i.ytimg.com/vi/Av9UFzl_wis/hqdefault.jpg")
    private String thumbnail;

    @Schema(description = "강의 제목", example = "네트워크 기초(개정판)")
    private String lectureTitle;

    @Schema(description = "영상 길이", example = "44:23")
    private int duration;

    @Schema(description = "회원 전용 강의 표시", example = "false")
    @JsonProperty("is_members_only")
    private boolean membership;
}
