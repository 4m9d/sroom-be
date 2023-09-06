package com.m9d.sroom.material.exception;

import com.m9d.sroom.global.error.NotFoundException;

public class QuizNotFoundException extends NotFoundException {

    private static final String MESSAGE = "해당 퀴즈를 찾을 수 없습니다.";

    public QuizNotFoundException() {
        super(MESSAGE);
    }
}
