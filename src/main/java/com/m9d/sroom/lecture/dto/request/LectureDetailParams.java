package com.m9d.sroom.lecture.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LectureDetailParams {

    @Parameter(in = ParameterIn.QUERY, name = "is_playlist", description = "플레이리스트 여부", required = true, example = "false")
    @NotNull
    @JsonProperty("is_playlist")
    private boolean isPlaylist;

    @Parameter(in = ParameterIn.QUERY, name = "index_only", description = "목차만 응답 여부", required = false, example = "false")
    @JsonProperty("index_only")
    private boolean indexOnly = false;

    @Parameter(in = ParameterIn.QUERY, name = "review_only", description = "후기만 응답 여부", required = false, example = "false")
    @JsonProperty("review_only")
    private boolean reviewOnly = false;

    @Parameter(in = ParameterIn.QUERY, name = "index_limit", description = "결과의 최대 개수", required = false, example = "50")
    @JsonProperty("index_limit")
    private int indexLimit = 50;

    @Parameter(in = ParameterIn.QUERY, name = "index_next_token", description = "목차 페이지 토큰", required = false, example = "CVBSOE")
    @JsonProperty("index_next_token")
    private String indexNextToken;

    @Parameter(in = ParameterIn.QUERY, name = "review_offset", description = "후기 오프셋", required = false, example = "9")
    @JsonProperty("review_offset")
    private int reviewOffset = 0;

    @Parameter(in = ParameterIn.QUERY, name = "review_limit", description = "후기의 최대 개수", required = false, example = "10")
    @JsonProperty("review_limit")
    private int reviewLimit = 10;
}
