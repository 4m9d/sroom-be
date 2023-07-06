package com.m9d.sroom.lecture.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "검색된 개별 강의 정보")
@Data
public class Lecture{

    @Schema(description = "강의 제목", example = "네트워크 기초(개정판)")
    private String lectureTitle;

    @Schema(description = "강의 설명", example = "OSI 7계층에서 각 계층의 다양한 프로토콜들을 통해서 배우는 네트워크 기초에 대한 강의입니다.")
    private String description;

    @Schema(description = "채널", example = "따라하면서 배우는 IT")
    private String channel;

    @Schema(description = "강의 ID", example = "PL0d8NnikouEWcF1jJueLdjRIC4HsUlULi")
    private String lectureCode;

    @Schema(description = "플레이리스트 여부", example = "true")
    @JsonProperty("isPlaylist")
    private boolean isPlaylist;

    @Schema(description = "강의 평점", example = "4.3")
    private double rating;

    @Schema(description = "후기 개수", example = "44")
    private int reviewCount;

    @Schema(description = "강의 썸네일", example = "https://i.ytimg.com/vi/Av9UFzl_wis/hqdefault.jpg")
    private String thumbnail;

    @Builder

    public Lecture(String lectureTitle, String description, String channel, String lectureCode, boolean isPlaylist, double rating, int reviewCount, String thumbnail) {
        this.lectureTitle = lectureTitle;
        this.description = description;
        this.channel = channel;
        this.lectureCode = lectureCode;
        this.isPlaylist = isPlaylist;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.thumbnail = thumbnail;
    }
}
