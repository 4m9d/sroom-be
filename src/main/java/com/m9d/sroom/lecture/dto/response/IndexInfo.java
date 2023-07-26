package com.m9d.sroom.lecture.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "목차 정보")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexInfo {

    @Schema(description = "목차 리스트")
    private List<Index> indexList;

    @Schema(description = "다음 페이지 토큰", example = "EAAaBlBUOkNBbw")
    private String nextPageToken;

    @Schema(description = "총 재생 시간", example = "5:12:34")
    private String totalDuration;
}
