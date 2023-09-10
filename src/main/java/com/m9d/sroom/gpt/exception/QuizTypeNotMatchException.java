package com.m9d.sroom.gpt.exception;

public class QuizTypeNotMatchException extends Exception {

    private static final String MESSAGE = "응답받은 퀴즈의 타입이 적절하지 않습니다. 입력받은 타입 = ";

    public QuizTypeNotMatchException(int type) {
        super(MESSAGE + type);
    }
}
