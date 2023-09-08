package com.m9d.sroom.material.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScrapResult {

    private Long courseQuizId;

    @JsonProperty("is_scrapped")
    private boolean scrapped;
}
