package com.m9d.sroom.lecture.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class KeywordSearchRes {

    @ApiModelProperty(value = "응답 개수", example = "4")
    private int resultPerPage;

    @ApiModelProperty(value = "다음페이지 조회 토큰", example = "CAQQAA")
    private String nextPageToken;

    @ApiModelProperty(value = "이전페이지 조회 토큰", example = "TAQVQA")
    private String prevPageToken;

    @ApiModelProperty(value = "조회된 강의 리스트")
    private List<Lecture> lectures;

    @Builder
    public KeywordSearchRes(int resultPerPage, String nextPageToken, String prevPageToken, List<Lecture> lectures) {
        this.resultPerPage = resultPerPage;
        this.nextPageToken = nextPageToken;
        this.prevPageToken = prevPageToken;
        this.lectures = lectures;
    }
}