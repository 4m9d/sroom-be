package com.m9d.sroom.lecture.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
public class KeywordSearchParam {

    @NotNull(message = "키워드 파라미터가 입력되지 않았습니다.")
    private String keyword;

    private String nextPageToken;

    private int limit = 10;

    private String filter = "all";
}
