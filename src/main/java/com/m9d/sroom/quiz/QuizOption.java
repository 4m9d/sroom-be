package com.m9d.sroom.quiz;

import lombok.Getter;

@Getter
public class QuizOption {

    private final int index;

    private final boolean isCorrect;

    private final String content;

    public QuizOption(int index, boolean isCorrect, String content) {
        this.index = index;
        this.isCorrect = isCorrect;
        this.content = content;
    }
}
