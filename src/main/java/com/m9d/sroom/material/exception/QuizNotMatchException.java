package com.m9d.sroom.material.exception;

import com.m9d.sroom.common.error.NotMatchException;

public class QuizNotMatchException extends NotMatchException {

    private static final String MESSAGE = "입력받은 여러 퀴즈 id가 일치하지 않습니다.";
    public QuizNotMatchException() {
        super(MESSAGE);
    }
}
