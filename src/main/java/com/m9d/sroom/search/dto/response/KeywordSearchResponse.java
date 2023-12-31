package com.m9d.sroom.search.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "검색 결과 강의 리스트")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeywordSearchResponse {

    @Schema(description = "응답 개수", example = "5")
    private int resultPerPage;

    @Schema(description = "다음페이지 조회 토큰", example = "CAQQAA")
    private String nextPageToken;

    @Schema(description = "이전페이지 조회 토큰", example = "QAQQAA")
    private String prevPageToken;

    @Schema(description = "조회된 강의 리스트")
    private List<LectureResponse> lectures;
}