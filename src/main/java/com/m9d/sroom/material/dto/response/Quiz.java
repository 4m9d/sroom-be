package com.m9d.sroom.material.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Quiz {

    private Long quizId;

    private int quizType;

    private String quizQuestion;

    private String quizSelectOption1;

    private String quizSelectOption2;

    private String quizSelectOption3;

    private String quizSelectOption4;

    private String quizSelectOption5;

    @JsonProperty("is_submitted")
    private boolean submitted;

    private String answer;

    private String submittedAt;

    private String submittedAnswer;

    @JsonProperty("is_correct")
    private boolean correct;
}
