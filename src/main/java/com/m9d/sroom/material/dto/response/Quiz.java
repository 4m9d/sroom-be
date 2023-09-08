package com.m9d.sroom.material.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Quiz {

    private Long id;

    private int type;

    private String question;

    private List<String> options;

    @JsonProperty("is_submitted")
    private boolean submitted;

    private String answer;

    private String submittedAt;

    private String submittedAnswer;

    @JsonProperty("is_correct")
    private boolean correct;

    @JsonProperty("is_scrapped")
    private boolean scrapped;
}
