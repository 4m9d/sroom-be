package com.m9d.sroom.global.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuizOption {

    private Long quizOptionId;

    private Long quizId;

    private String optionText;

    private int index;
}
