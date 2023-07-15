package com.m9d.sroom.lecture.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "재생목록 목차 정보")
@Data
public class Index {

    @Schema(description = "목차 번호", example = "1")
    private int index;

    @Schema(description = "강의 썸네일", example = "https://i.ytimg.com/vi/Av9UFzl_wis/hqdefault.jpg")
    private String thumbnail;

    @Schema(description = "강의 제목", example = "네트워크 기초(개정판)")
    private String lectureTitle;

    @Schema(description = "영상 길이", example = "44:23")
    private String duration;

    @Builder
    public Index(int index, String thumbnail, String lectureTitle, String duration) {
        this.index = index;
        this.thumbnail = thumbnail;
        this.lectureTitle = lectureTitle;
        this.duration = duration;
    }
}
