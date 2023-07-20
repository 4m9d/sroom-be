package com.m9d.sroom.lecture.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Schema(description = "검색 결과 강의 리스트")
@Data
@Builder
public class KeywordSearch {

    @Schema(description = "응답 개수", example = "5")
    private int resultPerPage;

    @Schema(description = "다음페이지 조회 토큰", example = "CAQQAA")
    private String nextPageToken;

    @Schema(description = "이전페이지 조회 토큰", example = "QAQQAA")
    private String prevPageToken;

    @Schema(description = "조회된 강의 리스트")
    private List<Lecture> lectures;
}