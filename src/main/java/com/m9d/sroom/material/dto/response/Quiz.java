package com.m9d.sroom.material.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Quiz {

    private Long id;

    private int type;

    private String question;

    private String selectOption1;

    private String selectOption2;

    private String selectOption3;

    private String selectOption4;

    private String selectOption5;

    @JsonProperty("is_submitted")
    private boolean submitted;

    private String answer;

    private String submittedAt;

    private String submittedAnswer;

    @JsonProperty("is_correct")
    private boolean correct;
}
