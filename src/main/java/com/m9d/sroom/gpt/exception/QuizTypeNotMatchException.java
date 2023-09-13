package com.m9d.sroom.gpt.exception;

import com.m9d.sroom.global.error.NotMatchException;

public class QuizTypeNotMatchException extends NotMatchException {

    private static final String MESSAGE = "응답받은 퀴즈의 타입이 적절하지 않습니다. 입력받은 타입 = ";

    public QuizTypeNotMatchException(int type) {
        super(MESSAGE + type);
    }
}
