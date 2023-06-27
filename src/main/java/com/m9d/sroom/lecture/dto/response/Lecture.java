package com.m9d.sroom.lecture.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
public class Lecture{

    @ApiModelProperty(value = "강의 제목", example = "네트워크 기초(개정판)")
    private String lectureTitle;

    @ApiModelProperty(value = "강의 설", example = "OSI 7계층에서 각 계층의 다양한 프로토콜들을 통해서 배우는 네트워크 기초에 대한 강의입니다.")
    private String description;

    @ApiModelProperty(value = "강의 ID", example = "PL0d8NnikouEWcF1jJueLdjRIC4HsUlULi")
    private String lectureId;

    @ApiModelProperty(value = "플레이리스트 여부", example = "true")
    private boolean isPlaylist;

    @ApiModelProperty(value = "강의 평점", example = "4.3")
    private double rating;

    @ApiModelProperty(value = "후기 개수", example = "44")
    private int reviewCount;

    @ApiModelProperty(value = "강의 썸네일", example = "https://i.ytimg.com/vi/Av9UFzl_wis/hqdefault.jpg")
    private String thumbnail;

    @Builder
    public Lecture(String lectureTitle, String description, String lectureId, boolean isPlaylist, double rating, int reviewCount, String thumbnail) {
        this.lectureTitle = lectureTitle;
        this.description = description;
        this.lectureId = lectureId;
        this.isPlaylist = isPlaylist;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.thumbnail = thumbnail;
    }
}
