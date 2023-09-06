package com.m9d.sroom.material.exception;

import com.m9d.sroom.global.error.NotMatchException;

public class QuizIdNotMatchException extends NotMatchException {

    private static final String MESSAGE = "해당 영상에 대한 퀴즈가 아닙니다.";

    public QuizIdNotMatchException() {
        super(MESSAGE);
    }
}
